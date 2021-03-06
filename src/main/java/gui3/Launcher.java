package gui3;

import gui3.layout.GameWindowLayout;
import gui3.layout.LoginWindowLayout;
import gui3.layout.WindowLayout;
import gui3.login.LoginFacade;
import gui3.login.BasicLoginFacade;
import gui3.login.LoginListener;
import gui3.text.*;
import io.listener.StateListener;
import io.protocol.impl.BasicANSIProtocolHandler;
import io.protocol.impl.BasicMudClientFilter;
import io.protocol.impl.BasicMudClientModeStyle;
import io.protocol.impl.BasicTelnetProtocolHandler;
import io.protocol.impl.BasicTextSanitizer;
import io.protocol.impl.BetterMudClientProtocolHandler;
import io.sensor.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import backend2.CommandHistory;
import backend2.CommandSender;
import backend2.FesSender;
import backend2.HtmlLogger;
import backend2.InputOutput;
import backend2.Logger;
import backend2.RobustInputOutput;
import backend2.TriggerConfiguration;
import domain.Configuration;
import domain.State;


public class Launcher {
	public static final String VERSION = "v1.6.4";
	
	private LoginWindowLayout loginWindowLayout;
	private MainWindowWrapper mainFrame;
	private BetterMudClientProtocolHandler mudClientProtocolHandler;
	private BasicTextSanitizer textSanitizer;
	private LoginFacade loginHandler;
	private CommandSender commandSender;
	private State state;
	private RobustInputOutput io;

	private LoginWrapper loginWrapper;
	private Configuration configuration;
	private String host;
	private Logger logger;

	public void init() {
		boolean isCommandHistoryEnabled = false;
		
		// ------- constructors

		Map mapIdToComponent = new HashMap();
		loginWindowLayout = new LoginWindowLayout();
		GameWindowLayout gameWindowLayout = new GameWindowLayout();
		mainFrame = new MainWindowWrapper();
		StatusBarWrapper statusBar = new StatusBarWrapper();
		QuickKeyWrapper quickKeyWrapper = new QuickKeyWrapper();
		BetterTextAreaDocument textDocument = new BetterTextAreaDocument();
		TextAreaWrapper scrollback = new TextAreaWrapper();
		TextAreaWrapper mainText = new TextAreaWrapper();
		Prompt prompt = new Prompt();
		ConfigurationWrapper configurationWrapper = new ConfigurationWrapper();
		Panel southPanel = new Panel();
		Panel northPanel = new Panel();
		CenterPanel centerPanel = new CenterPanel();
		ScrollbarWrapper scrollbarWrapper = new ScrollbarWrapper();
		ScrollbackController scrollbackController = new ScrollbackController();
		loginWrapper = new LoginWrapper();
		LineDetector lineDetector = new LineDetector();

		scrollback.setScrollbackController(scrollbackController);
		mainText.setScrollbackController(scrollbackController);

		ColourHelper colourHelper = new ConfigurableColourHelper( new DefaultColourHelper() );
		FocusRetargetter focusRetargetter0 = new FocusRetargetter();
		FocusRetargetter focusRetargetter1 = new FocusRetargetter();
		FocusRetargetter focusRetargetter2 = new FocusRetargetter();
		FontManager fontManager = new FontManager();
		configuration = new Configuration();
		
		io = new RobustInputOutput();
		TriggerConfiguration triggerConfiguration = new TriggerConfiguration();
		FesSender fesSender = new FesSender();
		SnoopHandler snoopHandler = new SnoopHandler();
		CommandHistory commandHistory = new CommandHistory();
		loginHandler = new BasicLoginFacade();
		loginHandler.setConfiguration(configuration);
		
		CommandSender uiCommandSender;
		
		if (isCommandHistoryEnabled) {
			uiCommandSender = commandHistory; 
		} else {
			uiCommandSender = fesSender;
		}
		
		BasicTelnetProtocolHandler telnetProtocolHandler = new BasicTelnetProtocolHandler();
		BasicANSIProtocolHandler ansiProtocolHandler = new BasicANSIProtocolHandler();
		mudClientProtocolHandler = new BetterMudClientProtocolHandler();
		BasicMudClientModeStyle mudClientModeStyle = new BasicMudClientModeStyle();
		textSanitizer = new BasicTextSanitizer();
		BasicMudClientFilter mudClientFilter = new BasicMudClientFilter();
		state = new State();
		StatsSensor statsSensor = new StatsSensor();
		PlayerSensor playerSensor = new PlayerSensor();

		SyncSensor syncSensor = new SyncSensor();
		syncSensor.setState(state);

		PlanSensor planSensor = new PlanSensor();
		planSensor.setConfiguration(configuration);

		logger = new HtmlLogger();
		statsSensor.addStateListener((StateListener) logger);
		
		ConfigurationWindowWrapper configurationFrame = new ConfigurationWindowWrapper();

		CommandTransformer commandTransformer = new CommandTransformer();

		commandTransformer.setConfiguration(configuration);
		commandTransformer.setLineDetector(lineDetector);
		prompt.setCommandTransformer(commandTransformer);
		commandTransformer.init();

		MausoleumPuzzleSensor mausoleumPuzzleSensor = new MausoleumPuzzleSensor();
		mausoleumPuzzleSensor.setConfiguration(configuration);
		mausoleumPuzzleSensor.setCommandTransformer(commandTransformer);
		mausoleumPuzzleSensor.setLineDetector(lineDetector);
//		mudClientFilter.addTextListener(mausoleumPuzzleSensor);
		mausoleumPuzzleSensor.setCommandSender(uiCommandSender);
		uiCommandSender = mausoleumPuzzleSensor; // intercept commands sent

		commandSender = io;

		commandTransformer.setCommandSender(commandSender);

		// ------- setters

		host = System.getenv("MUD2_HOST");
		if (host == null) {
			host = System.getProperty("host", "mudii.co.uk"); // for backwards compatibility
		}

		io.setHost(host);
		io.setTelnetProtocolHandler(telnetProtocolHandler);
		io.setMudClientFilter(mudClientFilter);
		if (System.getProperty("debug") != null) {
			mudClientProtocolHandler.addTextListener(io);
		} else {
			mudClientFilter.addTextListener(io);
		}
		if (System.getProperty("debug") != null) {
			mudClientFilter.addCodeListener(io);
		}
		if (configuration.getInt("sync", 0) != 0) {
			state.addStateListener(io);
		}
		
//		io.addTrigger(".*^EXAMINE>.*", "q\r");
//		io.addTrigger(".*^Players:.*", "fes\r");
		
//		URL systemResource = ClassLoader.getSystemResource(System.getProperty("host", "mudii.co.uk")+"-triggers.txt");
//		if (systemResource != null) {
//			triggerConfiguration.setFile(systemResource.getFile());
//		}
//		triggerConfiguration.setInputOutput(io);
		
		fesSender.setCommandSender(io);
		fesSender.setConfiguration(configuration);
		state.addStateListener(fesSender);
		
		// initialise early so that we can detect PLAYING state and disable/enable output
		textSanitizer.addTextListener(statsSensor);
		textSanitizer.addCodeListener(statsSensor);
		textSanitizer.addCodeListener(syncSensor);
		textSanitizer.addTextListener(planSensor);

		statsSensor.addStateListener(state);

		if (System.getProperty("headless", "false").equals("false")) {
			io.addOutputListener(snoopHandler);
			mudClientProtocolHandler.addCodeListener(snoopHandler);
		}

		telnetProtocolHandler.addBytesListener(ansiProtocolHandler);
		ansiProtocolHandler.addBytesListener(mudClientProtocolHandler);
		mudClientProtocolHandler.addCodeListener(mudClientFilter);
		mudClientProtocolHandler.addTextListener(mudClientFilter);

		statsSensor.addCodeListener(mudClientModeStyle);
		mudClientProtocolHandler.addCodeListener(textSanitizer);
		mudClientProtocolHandler.addTextListener(textSanitizer);

		textSanitizer.addTextListener(playerSensor);
		textSanitizer.addCodeListener(playerSensor);
		
		if (configuration.getInt(Configuration.KEY_LOGGING, Configuration.DEFAULT_LOGGING) == 1) {
			String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
			String logDirectory = configuration.getSetting(Configuration.KEY_LOG_DIRECTORY, configuration.getDefaultLogDirectory());
			String filename = date + "-" + host + ".html";
			if (logDirectory.isEmpty()) {
				logger.setFilename(filename);
			} else {
				File logDir = new File(logDirectory);
				if (!logDir.exists()) {
					logDir.mkdirs();
				}
				logger.setFilename(logDirectory + File.separator + filename);
			}
			logger.setColourHelper(colourHelper);
			mudClientFilter.addTextListener(logger);
			mudClientFilter.addCodeListener(logger);
			mudClientModeStyle.addStyleListener(logger);
		}
		
		configurationFrame.setParent(mainFrame);
		configurationFrame.setConfiguration(configuration);
		configurationFrame.setColourHelper(colourHelper);
		
		commandHistory.setCommandSender(fesSender);
//		commandHistory.setConfiguration(configuration);
		commandHistory.setState(state);

		loginHandler.setInputOutput(io);
		loginHandler.setHostComponent(mainFrame);
		io.addOutputListener(loginHandler);
		
		mapIdToComponent.put(WindowLayout.KEY_MAINFRAME, mainFrame);
		mapIdToComponent.put(WindowLayout.KEY_CONFIG, configurationWrapper);
		mapIdToComponent.put(WindowLayout.KEY_PROMPT, prompt);
		mapIdToComponent.put(WindowLayout.KEY_SCROLL_BAR, scrollbarWrapper);
		mapIdToComponent.put(WindowLayout.KEY_STATUS_BAR, statusBar);
		mapIdToComponent.put(WindowLayout.KEY_NORTH_PANEL, northPanel);
		mapIdToComponent.put(WindowLayout.KEY_SOUTH_PANEL, southPanel);
		mapIdToComponent.put(WindowLayout.KEY_CENTER_PANEL, centerPanel);
		mapIdToComponent.put(WindowLayout.KEY_LOGIN, loginWrapper);
		if (isCommandHistoryEnabled) {
			mapIdToComponent.put(WindowLayout.KEY_QUICK_KEY, quickKeyWrapper);
		}
		gameWindowLayout.setMapIdToComponent(mapIdToComponent);
		loginWindowLayout.setMapIdToComponent(mapIdToComponent);
				
		mainFrame.setConfiguration(configuration);

//		northPanel.setParent(mainFrame);
//		southPanel.setParent(mainFrame);
		
//		statusBar.setParent(mainFrame);
		statusBar.setState(state);
		statusBar.setColourHelper(colourHelper);
		statusBar.setParent(mainFrame);
		fontManager.addFontConsumer(statusBar);
		
//		quickKeyWrapper.setMainFrame(mainFrame);
		if (isCommandHistoryEnabled) {
			quickKeyWrapper.setCommandHistory(commandHistory);
			state.addStateListener(quickKeyWrapper);
			fontManager.addFontConsumer(quickKeyWrapper);
		}
		
		scrollback.setScrollback(true);
		scrollback.setDocument(textDocument);
		scrollback.setColourHelper(colourHelper);
		fontManager.addFontConsumer(scrollback);
//		io.addOutputListener(scrollback);
		snoopHandler.addOutputListener(scrollback);
		state.addStateListener(scrollback);
		scrollback.setConfiguration(configuration);
		
		//		mainText.setParent(mainFrame);
		mainText.setDocument(textDocument);
		mainText.setColourHelper(colourHelper);
		mainText.setPlan(planSensor);
//		io.addOutputListener(mainText);
		snoopHandler.addOutputListener(mainText);
		ansiProtocolHandler.addStyleListener(mainText);
		mudClientModeStyle.addStyleListener(mainText);
		fontManager.addFontConsumer(mainText);
		snoopHandler.addPrefixListener(mainText);
		fontManager.addFontConsumer(snoopHandler);
		snoopHandler.setConfiguration(configuration);
		snoopHandler.setColourHelper(colourHelper);
		snoopHandler.setMudClientModeStyle(mudClientModeStyle);
		snoopHandler.setAnsiProtocolHandler(ansiProtocolHandler);
		state.addStateListener(mainText);
		mainText.setConfiguration(configuration);
		
		centerPanel.setParent(mainFrame);
		centerPanel.setScrollback(scrollback);
		centerPanel.setMainText(mainText);
		
		scrollbackController.setCenterPanel(centerPanel);
		scrollbackController.setEnabled(false);
		scrollbackController.setScrollback(scrollback);
		scrollbackController.setScrollbarWrapper(scrollbarWrapper);

//		prompt.setParent(southPanel);
		//prompt.setCommandSender(io);
		prompt.setCommandSender(uiCommandSender);
		prompt.setScrollbackController(scrollbackController);
		prompt.setFontManager(fontManager);
		prompt.setConfiguration(configuration);
		prompt.setState(state);
		if (isCommandHistoryEnabled) {
			prompt.setFunctionKeyStore(quickKeyWrapper);
		}
		if (System.getProperty("headless", "false").equals("false")) {
			playerSensor.addPlayerSensor(prompt);
			state.addStateListener(prompt);
		}
		
		fontManager.addFontConsumer(prompt);
		
//		configurationWrapper.setParent(southPanel);
		configurationWrapper.setWindow(configurationFrame);
		
//		scrollbarWrapper.setParent(mainFrame);
		scrollbarWrapper.setScrollback(scrollback);
		scrollbarWrapper.setScrollbackController(scrollbackController);
		textDocument.addDocumentListener(scrollbarWrapper);
		
		loginWrapper.setLoginHandler(loginHandler);
		loginWrapper.setGameWindowLayout(gameWindowLayout);
		loginWrapper.setHost(host);
		loginWrapper.setConfiguration(configuration);
		
		focusRetargetter0.setFrom(mainFrame);
		focusRetargetter0.setTo(prompt);
		
		focusRetargetter1.setFrom(mainText);
		focusRetargetter1.setTo(prompt);
		
		focusRetargetter2.setFrom(configurationWrapper);
		focusRetargetter2.setTo(prompt);
		
		fontManager.addFontConsumer(mainFrame); // order is important here
		
		fontManager.setConfiguration(configuration);

		statsSensor.addStateListener(lineDetector);
		mudClientFilter.addCodeListener(lineDetector);
		mudClientFilter.addTextListener(lineDetector);

		// ------- initialisers

		planSensor.init();

		if (System.getProperty("headless", "false").equals("false")) {

			mainFrame.init();
			statusBar.init();
			if (isCommandHistoryEnabled) {
				quickKeyWrapper.init();
			}
			scrollback.init();
			mainText.init();

			northPanel.init();
			centerPanel.init();
			southPanel.init();
			scrollbackController.init();

			scrollbarWrapper.init();

			prompt.init();
			configurationWrapper.init();

			loginWrapper.init();

			focusRetargetter0.init();
			focusRetargetter1.init();
			focusRetargetter2.init();

			fontManager.init();

			configurationFrame.init();

			if (isCommandHistoryEnabled) {
				commandHistory.init();
			}
		}
		
		if (configuration.getInt(Configuration.KEY_LOGGING, Configuration.DEFAULT_LOGGING) == 1) {
			logger.init();
		}
		
		io.init();
		
		triggerConfiguration.init();

		mausoleumPuzzleSensor.init();
	}
	
	public void run() {
		if (System.getProperty("headless", "false").equals("false")) {
			loginWindowLayout.doLayout();

			mainFrame.show();
		}
		
		if (System.getProperty("quicktest") != null) {
			if (System.getProperty("headless", "false").equals("false")) {
				loginWrapper.loginAction();
			} else {
				loginHandler.setSystemUser(configuration.getSetting(host + ".system.user", "mud"));
				loginHandler.setSystemPassword(configuration.getSetting(host + ".system.password", ""));
				loginHandler.setAccountUser(configuration.getSetting(host+".account.user", ""));
				loginHandler.setAccountPassword(configuration.getSetting(host+".account.password", ""));
				loginHandler.login();
			}
		}
	}

	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.init();
		launcher.run();
	}

	public void addSensor(Sensor sensor) {
		textSanitizer.addTextListener(sensor);
		textSanitizer.addCodeListener(sensor);
	}

	public void addLoginListener(LoginListener loginListener ) {
		loginHandler.addLoginListener(loginListener);
	}

	public CommandSender getCommandSender() {
		return commandSender;
	}

	public State getState() {
		return state;
	}

	public InputOutput getInputOutput() {
		return io;
	}

	public Logger getLogger() {
		return logger;
	}
}
