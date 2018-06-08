package backend2;

import gui3.login.FiniteStateMachine;
import gui3.login.LoginDetails;
import gui3.login.LoginFacade;
import gui3.login.StateTransition;
import io.listener.BytesListener;
import io.protocol.TelnetProtocolHandler;
import io.protocol.impl.BasicTelnetProtocolHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gui3.login.LoginFacade.STATE_START;

public class MudHostScout implements CommandSender, BytesListener {
    private final String host;
    private final int port;
    private final String timezone;
    private OutputStream outputStream;
    private StringBuffer buffer = new StringBuffer();
    private String state = STATE_START;
    private FiniteStateMachine stateMachine;
    private long pingTime;
    private List<Integer> pingTimes = new ArrayList<>();

    public MudHostScout(String host, int port, String timezone) {
        this.host = host;
        this.port = port;
        this.timezone = timezone;

        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setSystemUser("mudguest");
        stateMachine = build(loginDetails);
    }

    private void run() throws IOException {
        Socket socket = new Socket(host, port);
        outputStream = socket.getOutputStream();
        BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());

        TelnetProtocolHandler telnetProtocolHandler = new BasicTelnetProtocolHandler();
        telnetProtocolHandler.setOutputStream(outputStream);
        telnetProtocolHandler.addBytesListener(this);

        byte[] byteBuffer = new byte[1024];

        while(true) {
            int length = inputStream.read(byteBuffer);
            if (length == -1) {
                break;
            }
            telnetProtocolHandler.onBytes(byteBuffer, 0, length);
        }
    }

    public FiniteStateMachine build(LoginDetails loginDetails) {
        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();

        StateTransition transitionEnterClientMode = createTransition("Option", "", LoginFacade.AUTO_PLAY);
        StateTransition transitionFinishNews = createTransition("Hit return.", "", LoginFacade.STATE_POST_NEWS);
        StateTransition transitionSkipNews = createTransition("Skip the rest\\? \\(y/n\\)", "n", LoginFacade.STATE_POST_SKIP_NEWS_ITEM);
        StateTransition transitionAccountPassword = createTransition("Account ID:", loginDetails.getAccountUser(), LoginFacade.STATE_POST_ACCOUNT_USER);

        finiteStateMachine.addTransition(STATE_START, createTransition("login:", loginDetails.getSystemUser(), LoginFacade.STATE_POST_LOGIN));

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, transitionEnterClientMode);
        finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, createTransition("Password:", loginDetails.getSystemPassword(), LoginFacade.STATE_POST_SYSTEM_PASSWORD));
        finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, transitionSkipNews);
        finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, createTransition("Hit return.", "", LoginFacade.STATE_POST_SKIP_NEWS_ITEM));

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_SYSTEM_PASSWORD, createTransition("@tesuji:~\\$", "/usr/bin/mudlogin", LoginFacade.STATE_POST_SYSTEM_PASSWORD));
        finiteStateMachine.addTransition(LoginFacade.STATE_POST_SYSTEM_PASSWORD, transitionAccountPassword);

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, transitionAccountPassword);

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_USER, createTransition("Password:", loginDetails.getAccountPassword(), LoginFacade.STATE_POST_ACCOUNT_PASSWORD));

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, transitionEnterClientMode);
        finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, transitionSkipNews);

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, transitionFinishNews);

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_SKIP_NEWS_ITEM, transitionFinishNews);
        finiteStateMachine.addTransition(LoginFacade.STATE_POST_SKIP_NEWS_ITEM, transitionEnterClientMode);

        finiteStateMachine.addTransition(LoginFacade.STATE_POST_NEWS, transitionEnterClientMode);

        finiteStateMachine.addTransition(LoginFacade.AUTO_PLAY, createTransition("Option", "p", "USERNAME_PROMPT"));

        finiteStateMachine.addTransition("USERNAME_PROMPT", createTransition("\\*", "q", "PING_PREPARE"));
        finiteStateMachine.addTransition("PING_PREPARE", new StateTransition("Option", this) {
            @Override
            public String execute() {
                return "PING_READY";
            }
        });
        finiteStateMachine.addTransition("PING_READY", new StateTransition("x", this) {
            @Override
            public String execute() {
                return "PONG_READY";
            }
        });
        finiteStateMachine.addTransition("PONG_READY", new StateTransition("y", this) {
            @Override
            public String execute() {
                return "PING_READY";
            }
        });

        return finiteStateMachine;
    }

    private StateTransition createTransition(String pattern, String response, String destinationState) {
        return new SimpleLoginTrigger(pattern, this, response, destinationState);
    }

    public static void main(String[] args) throws IOException {
        new MudHostScout("mudii.co.uk", 23, "Europe/London").run(); // 157-159-160-162-660
//        new MudHostScout("mud2.com", 27723, "America/Toronto").run(); // 90-96-185-194-702
    }

    @Override
    public void send(String text) {
        try {
            outputStream.write(text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBytes(byte[] bytes, int offset, int length) {
        buffer.append(new String(bytes, offset, length));
//        System.out.println("buffer: "+buffer.toString());

        StateTransition matchingTransition = stateMachine.findMatchingTransition(state, buffer.toString());

        if (matchingTransition != null) {
            String priorState = state;
            state = matchingTransition.execute();
            if (priorState != state) {
//                System.out.println(priorState+" -> "+state);
                if ("USERNAME_PROMPT".equals(priorState)) {
                    onMudResetInfo(buffer.toString());
                }
                if ("PING_READY".equals(priorState) || "PONG_READY".equals(priorState)) {
                    int lagTime = (int) (System.currentTimeMillis() - pingTime);
                    pingTimes.add(lagTime);
                    Collections.sort(pingTimes);
                    if (pingTimes.size() > 250) {
                        pingTimes.remove((int)(pingTimes.size()*Math.random()));
                    }
                    int secondIndex = (int)(pingTimes.size() * 0.02);
                    int twentyFifthIndex = (int)(pingTimes.size() * 0.25);
                    int medianIndex = (int)(pingTimes.size() * 0.5);
                    int seventyFifthIndex = (int)(pingTimes.size() * 0.75);
                    int ninetyEightIndex = (int)(pingTimes.size() * 0.98);
                    System.out.println("Lag time: "+ lagTime+"\t"
                            +(pingTimes.get(secondIndex)
                            + "-" + (pingTimes.get(twentyFifthIndex)
                            + "-" + pingTimes.get(medianIndex))
                            + "-" + pingTimes.get(seventyFifthIndex)
                            + "-" + pingTimes.get(ninetyEightIndex)));
                }
                if ("PING_READY".equals(state)) {
                    pingTime = System.currentTimeMillis();
                    send("\bx");
                }
                if ("PONG_READY".equals(state)) {
                    pingTime = System.currentTimeMillis();
                    send("\by");
                }
            }
            buffer.delete(0, buffer.length());
        }
    }

    private void onMudResetInfo(String text) {
        Matcher matcher = Pattern.compile(".*MUD last reset on (.*?) at (.*?)\\..*", Pattern.DOTALL | Pattern.MULTILINE).matcher(text);
        if (!matcher.matches()) {
            throw new IllegalStateException("No match found in: "+text);
        }

        String date = matcher.group(1);
        String time = matcher.group(2);
        int pos = date.indexOf("-");
        date = date.substring(0, pos+2) + date.substring(pos+2).toLowerCase();
        String dateTime = date + " " + time + " "+timezone;
//        System.out.println(dateTime);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ofPattern("d-MMM-yyyy HH:mm:ss VV"));
//        System.out.println(zonedDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        System.out.println(zonedDateTime.toEpochSecond());

    }
}
