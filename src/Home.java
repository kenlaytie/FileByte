import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Scanner;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Home {

    // Settings

    private String AppName;
    private String Destination;
    private int Port;
    private Boolean Wifi;
    private Boolean Ethernet;
    private Boolean Search;
    private String[] Addresses = { "", "" };

    // Configuration

    private ServerSocket serverSocket;
    private Socket socket = new Socket();

    Thread serverThread, clientThread, codeThread;
    String sentFile;
    int sentByte;

    private Boolean onSocket;

    // IU Variables

    private ToggleGroup toggleGroup = new ToggleGroup();
    public StackPane loadingPane;
    private VBox deviceVBox, historyVBox;
    private HBox[] HBOX = { new HBox(), new HBox() };
    private Tab shareTab;
    private TabPane homeTabPane;
    private Label LABEL, CODE;
    private Label[] LABELS = { new Label(), new Label() };
    private RadioButton helpButton;
    BooleanProperty booleanProperty;

    public Home() {

        // Get Settings

        try {
            // Get Name

            Scanner nameScanner = new Scanner(new File("src/Systems/settings/AppName/name.txt")).useDelimiter("//Z");
            AppName = nameScanner.next();
            if (AppName.equals("")) {
                AppName = System.getProperty("user.name");
            }

            // Get location

            Scanner locationScanner = new Scanner(new File("src/Systems/settings/Destination/location.txt"))
                    .useDelimiter("//Z");
            Destination = locationScanner.next();
            if (Destination.equals("")) {
                //
            }

            // Get Port

            Scanner portScanner = new Scanner(new File("src/Systems/settings/Connection/port.txt")).useDelimiter("//Z");
            Port = portScanner.nextInt();
            if (Port == 0) {
                Port = 3001;
            }

            // Get Wifi

            Scanner wifiScanner = new Scanner(new File("src/Systems/settings/Connection/wifi.txt")).useDelimiter("//Z");
            Wifi = wifiScanner.nextBoolean();

            // Get Wifi

            Scanner ethernetScanner = new Scanner(new File("src/Systems/settings/Connection/ethernet.txt"))
                    .useDelimiter("//Z");
            Ethernet = ethernetScanner.nextBoolean();

        } catch (Exception e) {
            System.out.print(e);
        }

        // App

        userExperience();
    }

    // XU Design

    private void userExperience() {
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: white;");

        // Menu

        StackPane menuPane = new StackPane();
        menuPane.setMaxWidth(230);
        menuPane.setMinWidth(230);
        menuPane.setEffect(new DropShadow(10, Color.DARKGRAY));
        menuPane.setStyle("-fx-background-color: rgb(252,252,252);");
        StackPane.setAlignment(menuPane, Pos.CENTER_LEFT);
        StackPane.setMargin(menuPane, new Insets(0, 0, 0, -190));
        menuPane.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                // TODO Auto-generated method stub
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), menuPane);
                transition.setToX(0);
                transition.play();
            }

        });
        stackPane.getChildren().addAll(menuPane);

        VBox menuBox = new VBox();
        menuPane.getChildren().add(menuBox);

        // Menu : Heading

        HBox headingBox = new HBox();
        headingBox.setPadding(new Insets(20, 0, 40, 15));
        headingBox.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: rgb(220,220,220);");
        menuBox.getChildren().add(headingBox);

        StackPane titlePane = new StackPane();
        titlePane.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titlePane, Priority.ALWAYS);

        HBox titleBox = new HBox();
        titleBox.setSpacing(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titlePane.getChildren().add(titleBox);

        ImageView logoView = new ImageView(new Image("files/logo/logo.png"));
        logoView.setFitHeight(25);
        logoView.setFitWidth(25);
        titleBox.getChildren().add(logoView);

        Label label = new Label("FileByte");
        label.setFont(Font.font("recoleta", 16));
        label.setTextFill(Color.GRAY);
        titleBox.getChildren().add(label);

        StackPane imgPane = new StackPane();
        imgPane.setPadding(new Insets(0, 10, 0, 10));
        imgPane.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), menuPane);
                transition.setToX(190);
                transition.play();
            }

        });

        ImageView imageView = new ImageView(new Image("files/icons/menu.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        imgPane.getChildren().add(imageView);

        headingBox.getChildren().addAll(titlePane, imgPane);

        // Menu : Content

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 0, 0, 0));
        VBox.setVgrow(vBox, Priority.ALWAYS);
        menuBox.getChildren().add(vBox);

        RadioButton homeButton = new RadioButton();
        homeButton.setSelected(true);
        menuButton(homeButton, "Home", "home.png");
        vBox.getChildren().add(homeButton);

        RadioButton deviceButton = new RadioButton();
        menuButton(deviceButton, "History", "people.png");
        vBox.getChildren().add(deviceButton);

        helpButton = new RadioButton();
        menuButton(helpButton, "Help", "about.png");
        vBox.getChildren().add(helpButton);

        RadioButton aboutButton = new RadioButton();
        menuButton(aboutButton, "About", "about_0.png");
        vBox.getChildren().add(aboutButton);

        // Menu : Settings

        StackPane settingsPane = new StackPane();
        settingsPane.setPadding(new Insets(0, 0, 20, 0));
        menuBox.getChildren().add(settingsPane);

        RadioButton settingsButton = new RadioButton();
        menuButton(settingsButton, "Settings", "settings.png");
        settingsPane.getChildren().add(settingsButton);

        // Content

        StackPane contPane = new StackPane();
        contPane.setPadding(new Insets(0, 0, 0, 40));
        stackPane.getChildren().add(0, contPane);

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");
        contPane.getChildren().add(tabPane);

        Tab homeTab = new Tab();
        homeUI(homeTab);
        homeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                tabPane.getSelectionModel().select(homeTab);
            }

        });

        Tab historyTab = new Tab();
        historyPage(historyTab);
        deviceButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                tabPane.getSelectionModel().select(historyTab);
            }

        });

        Tab learnTab = new Tab();
        learnPage(learnTab);
        helpButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                tabPane.getSelectionModel().select(learnTab);
            }

        });

        Tab abouTab = new Tab();
        aboutPage(abouTab);
        aboutButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                tabPane.getSelectionModel().select(abouTab);
            }

        });

        Tab settingsTab = new Tab();
        settingsPage(settingsTab);
        settingsButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                tabPane.getSelectionModel().select(settingsTab);
            }

        });

        tabPane.getTabs().addAll(homeTab, historyTab, learnTab, abouTab, settingsTab);

        // Blured Pane

        loadingPane = new StackPane();
        loadingPane.setStyle("-fx-background-color: rgb(31,31,31,0.4);");
        loadingPane.setVisible(false);

        stackPane.getChildren().add(loadingPane);

        Scene scene = new Scene(stackPane, 1000, 700);
        scene.getStylesheets().add("style.css");

        Stage stage = new Stage();
        stage.setTitle("FileByte");
        stage.getIcons().add(new Image("files/logo/logo.png"));
        stage.setScene(scene);
        stage.show();

    }

    // Menu Button

    private void menuButton(RadioButton button, String name, String fileName) {
        fileName = "files/icons/" + fileName;

        HBox hBox = new HBox();
        hBox.setSpacing(10);

        StackPane namePane = new StackPane();
        namePane.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(namePane, Priority.ALWAYS);
        hBox.getChildren().add(namePane);

        Label label = new Label(name);
        label.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        label.setTextFill(Color.rgb(80, 80, 80));
        namePane.getChildren().add(label);

        StackPane imgPane = new StackPane();
        hBox.getChildren().add(imgPane);

        ImageView imageView = new ImageView(new Image(fileName));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        imgPane.getChildren().add(imageView);

        button.setPrefWidth(250);
        button.setPadding(new Insets(10, 10, 10, 15));
        button.setGraphic(hBox);
        button.getStyleClass().remove("radio-button");
        button.getStyleClass().add("menu-button");
        button.setToggleGroup(toggleGroup);
        button.setCursor(Cursor.HAND);

    }

    // Home Page

    private void homeUI(Tab tab) {
        homeTabPane = new TabPane();
        homeTabPane.getStyleClass().addAll("tap-pane");
        tab.setContent(homeTabPane);

        Tab homeTab = new Tab();

        homeTabPane.getTabs().addAll(homeTab);

        StackPane stackPane = new StackPane();
        homeTab.setContent(stackPane);

        VBox vBox = new VBox();
        stackPane.getChildren().add(vBox);

        // Top Pane

        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(30));
        topPane.setStyle("-fx-background-color: snow;");
        VBox.setVgrow(topPane, Priority.ALWAYS);
        vBox.getChildren().add(topPane);

        HBox hBox = new HBox();
        hBox.setSpacing(60);
        topPane.getChildren().add(hBox);

        StackPane contPane = new StackPane();
        HBox.setHgrow(contPane, Priority.ALWAYS);
        hBox.getChildren().add(contPane);

        VBox onBox = new VBox();
        onBox.setPadding(new Insets(0, 0, 0, 100));
        onBox.setSpacing(10);
        onBox.setAlignment(Pos.CENTER_LEFT);
        contPane.getChildren().add(onBox);

        Label label = new Label("FILEBYTE");
        label.setFont(Font.font("Recoleta,Noto Serif,Georgia,Times New Roman,Times,serif", 45));
        label.setTextFill(Color.DODGERBLUE);
        onBox.getChildren().add(label);

        label = new Label("By BaseByte Team");
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        label.setTextFill(Color.STEELBLUE);
        onBox.getChildren().add(label);

        String infoString = "Easy share / transfer photos, music, video and any other document with filebyte. Use Filebyte for faster file sharing between devices";

        label = new Label(infoString);
        label.setMaxWidth(450);
        label.setFont(Font.font("", 14));
        label.setTextFill(Color.DARKGRAY);
        label.setWrapText(true);
        onBox.getChildren().add(label);

        Button contButton = new Button("How To Use");
        contButton.setPrefSize(200, 35);
        contButton.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        contButton.setTextFill(Color.WHITE);
        contButton.setStyle("-fx-background-color: steelblue;");
        contButton.setCursor(Cursor.HAND);
        VBox.setMargin(contButton, new Insets(10, 0, 0, 0));
        contButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                helpButton.setSelected(true);
                tab.getTabPane().getSelectionModel().select(2);
            }

        });
        onBox.getChildren().add(contButton);

        // Home Image

        StackPane imgPane = new StackPane();
        imgPane.setPadding(new Insets(0, 100, 0, 0));
        HBox.setHgrow(imgPane, Priority.ALWAYS);
        hBox.getChildren().add(imgPane);

        ImageView imageView = new ImageView(new Image("files/icons/shape_3.png"));
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        imgPane.getChildren().add(imageView);

        // Bottom Pane

        StackPane bottomPane = new StackPane();
        bottomPane.setPadding(new Insets(30));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);
        vBox.getChildren().add(bottomPane);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);
        bottomPane.getChildren().add(buttonBox);

        Button button = new Button("Create");
        button.setPrefSize(200, 35);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: dodgerblue;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                loadingPane.setVisible(true);
                loadUI();

                // Server Connection

                serverThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        try {
                            serverSocket = new ServerSocket(Port);
                            while (!serverSocket.isClosed()) {
                                socket = serverSocket.accept();
                                System.out.print("Client Connected..");

                                ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
                                String[] data = (String[]) stream.readObject();

                                if (data[0].equals("FILEBYTE") && !data[1].equals("")) {
                                    String[] strings = { "true", AppName };
                                    ObjectOutputStream outputStream = new ObjectOutputStream(
                                            socket.getOutputStream());
                                    outputStream.writeObject(strings);
                                    outputStream.flush();
                                    Search = false;

                                    Platform.runLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            loadingPane.getChildren().clear();
                                            loadingPane.setVisible(false);
                                            shareTab = new Tab();
                                            sharePage(shareTab, data[1]);
                                            homeTabPane.getTabs().add(shareTab);
                                            homeTabPane.getSelectionModel().select(shareTab);
                                            serverThread.interrupt();
                                        }

                                    });
                                    break;
                                } else {
                                    System.out.print("Not Filebyte");
                                }
                            }
                        } catch (Exception e) {
                            System.out.print(e);
                        }
                    }

                });
                serverThread.start();

                // Code Thread

                codeThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Search = true;
                        while (true) {
                            try {
                                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                                while (interfaces.hasMoreElements()) {
                                    NetworkInterface networkInterface = interfaces.nextElement();
                                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                                    while (addresses.hasMoreElements()) {
                                        InetAddress inetAddress = addresses.nextElement();

                                        if (Wifi == true) {
                                            if (networkInterface.getName()
                                                    .substring(0, Math.min(networkInterface.getName().length(), 4))
                                                    .equals("wlan")) {
                                                InetAddressValidator validator = InetAddressValidator.getInstance();
                                                if (validator.isValidInet4Address(inetAddress.getHostAddress())) {
                                                    String[] split = inetAddress.getHostAddress().split("\\.");

                                                    if (split[0].equals("192") && split[1].equals("168")) {
                                                        Platform.runLater(new Runnable() {

                                                            @Override
                                                            public void run() {

                                                                CODE.getParent().setVisible(false);
                                                                HBOX[0].setVisible(true);
                                                                LABELS[0].setText(split[2] + "-" + split[3]);

                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                        if (Ethernet == true) {
                                            if (networkInterface.getName()
                                                    .substring(0, Math.min(networkInterface.getName().length(), 3))
                                                    .equals("eth")) {
                                                InetAddressValidator validator = InetAddressValidator.getInstance();
                                                if (validator.isValidInet4Address(inetAddress.getHostAddress())) {
                                                    String[] split = inetAddress.getHostAddress().split("\\.");

                                                    if (split[0].equals("169") && split[1].equals("254")) {
                                                        Platform.runLater(new Runnable() {

                                                            @Override
                                                            public void run() {

                                                                CODE.getParent().setVisible(false);
                                                                HBOX[1].setVisible(true);
                                                                LABELS[1].setText(split[2] + "-" + split[3]);

                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (Search != true) {
                                break;
                            }
                        }

                    }

                });
                codeThread.start();
            }

        });
        buttonBox.getChildren().add(button);

        button = new Button("Connect");
        button.setPrefSize(200, 35);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.rgb(80, 80, 80));
        button.setEffect(new DropShadow(3, Color.DARKGRAY));
        button.setStyle("-fx-background-color: white;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                // Client Connection

                clientThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            onSocket = true;
                            int num = 0;
                            while (true) {
                                if (onSocket == true) {

                                    if (num % 2 == 0) {
                                        if (Wifi == true && !Addresses[0].equals("")) {
                                            try {
                                                socket = new Socket(Addresses[0], Port);
                                            } catch (Exception e) {
                                                System.out.print(e);
                                            }
                                        }
                                    } else {
                                        if (Ethernet == true && !Addresses[0].equals("")) {
                                            try {
                                                socket = new Socket(Addresses[1], Port);
                                            } catch (Exception e) {
                                                System.out.print(e);
                                            }
                                        }
                                    }

                                    if (socket.isConnected()) {
                                        System.out.print("Connected");

                                        String[] data = { "FILEBYTE", AppName };
                                        ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
                                        stream.writeObject(data);
                                        stream.flush();

                                        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                                        String[] strings = (String[]) inputStream.readObject();

                                        if (strings[0].equals("true")) {
                                            Platform.runLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                    loadingPane.getChildren().clear();
                                                    loadingPane.setVisible(false);
                                                    shareTab = new Tab();
                                                    sharePage(shareTab, strings[1]);
                                                    homeTabPane.getTabs().add(shareTab);
                                                    homeTabPane.getSelectionModel().select(shareTab);
                                                    clientThread.interrupt();
                                                }

                                            });
                                            break;
                                        } else {
                                            socket.close();
                                        }

                                    }
                                } else {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.print(e);
                        }
                    }

                });

                // GET CODE

                TextField textField = new TextField();
                Button getButton = new Button("Connect");
                getButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        String getString = textField.getText();
                        if (!getString.equals("")) {
                            String[] split = getString.split("\\-");

                            Addresses[0] = "192.168." + split[0] + "." + split[1];
                            Addresses[1] = "169.254." + split[0] + "." + split[1];

                            waitingIU();

                            clientThread.start();
                        }

                    }

                });
                codeUI(textField, getButton);

            }

        });
        buttonBox.getChildren().add(button);
    }

    // Loading Pane

    private void loadUI() {
        StackPane stackPane = new StackPane();
        stackPane.setMaxSize(400, 400);
        stackPane.setPadding(new Insets(20, 20, 30, 20));
        stackPane.setStyle("-fx-border-radius: 7px; -fx-background-radius: 7px; -fx-background-color: white;");
        loadingPane.getChildren().addAll(stackPane);

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        stackPane.getChildren().addAll(vBox);

        // Cancel

        StackPane cancelPane = new StackPane();
        cancelPane.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().addAll(cancelPane);

        ImageView imageView = new ImageView(new Image("files/icons/close.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);

        Button closeButton = new Button();
        closeButton.setPadding(new Insets(0));
        closeButton.setGraphic(imageView);
        closeButton.setStyle("-fx-background-color: transparent;");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {
                    serverSocket.close();
                } catch (Exception e) {
                    System.out.print(e);
                }

                loadingPane.getChildren().clear();
                loadingPane.setVisible(false);
            }

        });
        cancelPane.getChildren().addAll(closeButton);

        // Title

        StackPane titlePane = new StackPane();
        vBox.getChildren().addAll(titlePane);

        Label label = new Label("Loading Devices");
        label.setFont(Font.font("recaleto", 15));
        label.setTextFill(Color.GRAY);
        titlePane.getChildren().addAll(label);

        // loading GIF

        StackPane gifPane = new StackPane();
        gifPane.setPadding(new Insets(0, 30, 0, 0));
        vBox.getChildren().addAll(gifPane);

        ImageView gifView = new ImageView(new Image("files/images/loading_0.gif"));
        gifView.setFitWidth(200);
        gifView.setFitHeight(18);
        gifPane.getChildren().addAll(gifView);

        // Code

        VBox codeBox = new VBox();
        codeBox.setSpacing(10);
        codeBox.setPadding(new Insets(0, 20, 20, 20));
        codeBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(codeBox, Priority.ALWAYS);
        vBox.getChildren().addAll(codeBox);

        StackPane codePane = new StackPane();
        codeBox.getChildren().addAll(codePane);

        CODE = new Label("Connect to Local Network ( WIFI or Ethernet )");
        CODE.setTextAlignment(TextAlignment.CENTER);
        CODE.setWrapText(true);
        CODE.setFont(Font.font("sans-selif", FontWeight.BOLD, 16));
        CODE.setTextFill(Color.GRAY);
        codePane.getChildren().addAll(CODE);

        codeFunction(HBOX[0], LABELS[0], "Wifi");
        codeBox.getChildren().addAll(HBOX[0]);

        codeFunction(HBOX[1], LABELS[1], "Ethernet");
        codeBox.getChildren().addAll(HBOX[1]);
    }

    // Codes

    private void codeFunction(HBox hBox, Label label, String name) {
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setVisible(false);

        StackPane boxPane = new StackPane();
        boxPane.setAlignment(Pos.CENTER_LEFT);
        boxPane.setPrefWidth(100);

        Label titleLabel = new Label(name);
        titleLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        titleLabel.setTextFill(Color.rgb(90, 90, 90));
        boxPane.getChildren().addAll(titleLabel);

        StackPane dotPane = new StackPane();
        dotPane.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(dotPane, Priority.ALWAYS);

        Label dotLabel = new Label(":");
        dotLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 16));
        dotLabel.setTextFill(Color.BLACK);
        dotPane.getChildren().addAll(dotLabel);

        label.setText("1234");
        label.setFont(Font.font("recaleto", 16));
        label.setTextFill(Color.DARKGRAY);

        hBox.getChildren().addAll(boxPane, dotPane, label);
    }

    // Device List Bar

    private void deviceList(HBox hBox, String name) {
        hBox.setPadding(new Insets(6, 8, 6, 8));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setEffect(new DropShadow(2, Color.DARKGRAY));
        hBox.setStyle("-fx-border-radius: 3px; -fx-background-radius: 3px; -fx-background-color: white;");
        hBox.setCursor(Cursor.HAND);
        deviceVBox.getChildren().addAll(hBox);

        Button button = new Button("" + name.charAt(0));
        button.setPrefSize(30, 30);
        button.setPadding(new Insets(0));
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-border-radius: 40px; -fx-background-radius: 40px; -fx-background-color: grey;");

        Label label = new Label(name);
        label.setFont(Font.font("sans-serif", FontWeight.BOLD, 16));

        hBox.getChildren().addAll(button, label);
    }

    // Getting Code

    private void codeUI(TextField textField, Button button) {

        StackPane stackPane = new StackPane();
        stackPane.setMaxSize(330, 230);
        stackPane.setStyle("-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-background-color: white;");
        stackPane.setPadding(new Insets(20));

        loadingPane.setVisible(true);
        loadingPane.getChildren().clear();
        loadingPane.getChildren().addAll(stackPane);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        stackPane.getChildren().addAll(vBox);

        // Heading

        StackPane headingPane = new StackPane();
        vBox.getChildren().addAll(headingPane);

        Label label = new Label("CODE");
        label.setFont(Font.font("recaleto", 20));
        label.setTextFill(Color.GRAY);
        headingPane.getChildren().addAll(label);

        // Input

        StackPane inputPane = new StackPane();
        vBox.getChildren().addAll(inputPane);

        textField.setPadding(new Insets(10));
        textField.setPrefHeight(40);
        textField.setPromptText("Enter Code");
        textField.setAlignment(Pos.CENTER);
        textField.setStyle(
                "-fx-border: none; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-background-color: rgb(235,235,235)");
        inputPane.getChildren().addAll(textField);

        // Buttons

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        vBox.getChildren().addAll(hBox);

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefSize(200, 35);
        cancelButton.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        cancelButton.setTextFill(Color.BLACK);
        cancelButton.setStyle("-fx-background-color: rgb(220,220,220);");
        cancelButton.setCursor(Cursor.HAND);
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                loadingPane.getChildren().clear();
                loadingPane.setVisible(false);
            }

        });
        hBox.getChildren().addAll(cancelButton);

        button.setPrefSize(200, 35);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: dodgerblue;");
        button.setCursor(Cursor.HAND);
        hBox.getChildren().addAll(button);

    }

    // Waiting Connection

    private void waitingIU() {
        StackPane stackPane = new StackPane();
        stackPane.setMaxSize(400, 300);
        stackPane.setPadding(new Insets(20, 20, 30, 20));
        stackPane.setStyle("-fx-border-radius: 7px; -fx-background-radius: 7px; -fx-background-color: white;");

        loadingPane.setVisible(true);
        loadingPane.getChildren().clear();
        loadingPane.getChildren().addAll(stackPane);

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        stackPane.getChildren().addAll(vBox);

        // Cancel

        StackPane cancelPane = new StackPane();
        cancelPane.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().addAll(cancelPane);

        ImageView imageView = new ImageView(new Image("files/icons/close.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);

        Button closeButton = new Button();
        closeButton.setPadding(new Insets(0));
        closeButton.setGraphic(imageView);
        closeButton.setStyle("-fx-background-color: transparent;");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                onSocket = false;

                loadingPane.getChildren().clear();
                loadingPane.setVisible(false);
            }

        });
        cancelPane.getChildren().addAll(closeButton);

        // Title

        StackPane titlePane = new StackPane();
        vBox.getChildren().addAll(titlePane);

        Label label = new Label("Loading Devices ..");
        label.setFont(Font.font("recaleto", 15));
        label.setTextFill(Color.GRAY);
        titlePane.getChildren().addAll(label);

        // loading GIF

        StackPane gifPane = new StackPane();
        gifPane.setPadding(new Insets(0, 0, 20, 0));
        VBox.setVgrow(gifPane, Priority.ALWAYS);
        vBox.getChildren().addAll(gifPane);

        ImageView gifView = new ImageView(new Image("files/images/load_1.gif"));
        gifView.setFitWidth(70);
        gifView.setFitHeight(65);
        gifPane.getChildren().addAll(gifView);
    }

    // Main Page

    private void sharePage(Tab tab, String name) {
        VBox vBox = new VBox();

        StackPane stackPane = new StackPane();
        tab.setContent(stackPane);

        BorderPane borderPane = new BorderPane();
        stackPane.getChildren().addAll(borderPane);

        // Right

        VBox rightBox = new VBox();
        rightBox.setMaxWidth(300);
        rightBox.setMinWidth(300);
        rightBox.setPadding(new Insets(20));
        rightBox.setSpacing(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setStyle("-fx-background-color: whitesmoke;");
        borderPane.setRight(rightBox);

        ImageView imageView = new ImageView(new Image("files/icons/upload_3.png"));
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        rightBox.getChildren().addAll(imageView);

        Label label = new Label("Drag & Drop");
        label.setFont(Font.font("recaleto", 20));
        label.setTextFill(Color.GRAY);
        rightBox.getChildren().addAll(label);

        label = new Label("or");
        label.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        label.setTextFill(Color.GRAY);
        VBox.setMargin(label, new Insets(-15, 0, 0, 0));
        rightBox.getChildren().addAll(label);

        Button shareButton = new Button("Share");
        shareButton.setPrefSize(150, 35);
        shareButton.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        shareButton.setTextFill(Color.WHITE);
        shareButton.setStyle("-fx-background-color: dodgerblue;");
        shareButton.setCursor(Cursor.HAND);
        shareButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub

                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select File");
                File file = chooser.showOpenDialog(new Stage());

                if (file != null) {
                    String fileName = file.getName();
                    sentFile = fileName;

                    Path path = Paths.get(file.getPath());
                    int size = 0;
                    try {
                        size = (int) Files.size(path);
                        sentByte = size;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // File UI

                    double fileSize = size / 1048576;
                    String[] strings = { fileName, fileSize + " MB", file.getAbsolutePath() };
                    StackPane loadPane = new StackPane();
                    StackPane loadsPane = new StackPane();
                    Label[] labels = { new Label("Sending"), new Label("0 %") };
                    fileUIX(vBox, loadPane, loadsPane, strings, labels);

                    Thread sendThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                int sentInt = (int) sentByte;
                                String[] data = { fileName, sentInt + "" };

                                System.out.print(data[0]);

                                ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
                                stream.writeObject(data);
                                stream.flush();

                                try {

                                    OutputStream outputStream = socket.getOutputStream();

                                    int count, total = 0;
                                    byte[] buffer = new byte[1024];
                                    BufferedInputStream inputStream = new BufferedInputStream(
                                            new FileInputStream(file));
                                    while ((count = inputStream.read(buffer)) >= 0) {
                                        outputStream.write(buffer);
                                        outputStream.flush();

                                        total = total + count;
                                        int rate = (total * 100) / sentInt;
                                        int getTotal = total;

                                        Platform.runLater(new Runnable() {

                                            @Override
                                            public void run() {

                                                labels[1].setText(rate + " %");
                                                if (sentInt == getTotal) {
                                                    labels[0].setText("finished");
                                                }
                                                double width = (rate * loadPane.getWidth()) / 100;
                                                loadsPane.setMaxWidth(width);
                                                loadPane.widthProperty().addListener(new ChangeListener<Number>() {

                                                    @Override
                                                    public void changed(ObservableValue<? extends Number> unknown,
                                                            Number oldNumber, Number currentNumber) {
                                                        double sum = (rate * currentNumber.doubleValue()) / 100;
                                                        loadsPane.setMaxWidth(sum);
                                                    }

                                                });
                                            }

                                        });
                                    }

                                    inputStream.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Saving

                                try {
                                    Scanner getScanner = new Scanner(new File("src/Systems/history/boolean.txt"))
                                            .useDelimiter("\\Z");
                                    Boolean getBoolean = getScanner.nextBoolean();

                                    String addString = ",";
                                    if (getBoolean != true) {
                                        addString = "";

                                        PrintWriter writer = new PrintWriter(
                                                new File("src/Systems/history/boolean.txt"));
                                        writer.print("true");
                                        writer.close();
                                    }

                                    Scanner scanner = new Scanner(new File("src/Systems/history/content.txt"))
                                            .useDelimiter("\\Z");
                                    String content = scanner.next();
                                    content = content.substring(0, content.length() - 1);

                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    String time = timestamp.toString();

                                    JSONObject object = new JSONObject();
                                    object.put("name", name);
                                    object.put("file", strings[0]);
                                    object.put("size", strings[1]);
                                    object.put("status", "Sent");
                                    object.put("time", time);
                                    object.put("location", file.getAbsolutePath());

                                    content = content + addString + object.toJSONString() + "]";

                                    PrintWriter writer = new PrintWriter(new File("src/Systems/history/content.txt"));
                                    writer.print(content);
                                    writer.close();

                                    Platform.runLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            booleanProperty.set(!booleanProperty.get());
                                        }

                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                System.out.print(e);
                            }
                        }

                    });
                    sendThread.start();
                }
            }

        });
        rightBox.getChildren().addAll(shareButton);

        // Center

        BorderPane centerBorderPane = new BorderPane();
        borderPane.setCenter(centerBorderPane);

        // Top

        HBox topBox = new HBox();
        topBox.setPadding(new Insets(20));
        topBox.setStyle("-fx-background-color: rgb(250,250,250);");
        centerBorderPane.setTop(topBox);

        VBox nameBox = new VBox();
        nameBox.setSpacing(0);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        topBox.getChildren().addAll(nameBox);

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("recaleto", 23));
        nameLabel.setTextFill(Color.STEELBLUE);
        nameBox.getChildren().addAll(nameLabel);

        LABEL = new Label("Connected");
        LABEL.setFont(Font.font("", 15));
        LABEL.setTextFill(Color.GRAY);
        nameBox.getChildren().addAll(LABEL);

        StackPane closePane = new StackPane();
        closePane.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(closePane, Priority.ALWAYS);
        topBox.getChildren().addAll(closePane);

        Button closeButton = new Button("Disconnect");
        closeButton.setPrefSize(140, 35);
        closeButton.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        closeButton.setTextFill(Color.WHITE);
        closeButton.setStyle("-fx-background-color: purple;");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub

                shareTab.getTabPane().getTabs().remove(shareTab);

                try {
                    socket.close();
                    if (serverSocket != null) {
                        if (!serverSocket.isClosed()) {
                            serverSocket.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        closePane.getChildren().addAll(closeButton);

        // Content

        StackPane centerPane = new StackPane();
        centerBorderPane.setCenter(centerPane);

        VBox contBox = new VBox();
        centerPane.getChildren().addAll(contBox);

        StackPane labelPane = new StackPane();
        labelPane.setPadding(new Insets(20, 20, 0, 20));
        labelPane.setAlignment(Pos.CENTER_LEFT);
        contBox.getChildren().addAll(labelPane);

        Label contLabel = new Label("Shared Files");
        contLabel.setFont(Font.font("", 15));
        labelPane.getChildren().addAll(contLabel);

        StackPane contentPane = new StackPane();
        VBox.setVgrow(contentPane, Priority.ALWAYS);
        contBox.getChildren().addAll(contentPane);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.getStyleClass().addAll("scroll-pane");
        contentPane.getChildren().addAll(scrollPane);

        StackPane contPane = new StackPane();
        contPane.setPadding(new Insets(20));
        scrollPane.setContent(contPane);

        vBox.setSpacing(10);
        contPane.getChildren().addAll(vBox);

        // File Share Connection

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    while (socket.isConnected()) {
                        ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
                        String[] data = (String[]) stream.readObject();
                        int size = Integer.parseInt(data[1]);
                        String fileName = data[0];

                        System.out.print(fileName);

                        double filesize = size / 1048576;
                        String[] strings = { fileName, filesize + " MB", Destination + "\\" + fileName };
                        StackPane loadPane = new StackPane();
                        StackPane loadsPane = new StackPane();
                        Label[] labels = { new Label("Recieving"), new Label("0 %") };

                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                fileUIX(vBox, loadPane, loadsPane, strings, labels);
                            }

                        });

                        try {
                            InputStream streams = socket.getInputStream();
                            FileOutputStream outputStream = new FileOutputStream(
                                    new File(Destination + "/" + fileName));

                            int count, total = 0;
                            byte[] buffer = new byte[1024];

                            while ((count = streams.read(buffer)) >= 0) {
                                outputStream.write(buffer, 0, count);

                                total = total + count;
                                int rate = (total * 100) / size;
                                int getTotal = total;

                                Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                        labels[1].setText(rate + " %");
                                        if (size == getTotal) {
                                            labels[0].setText("finished");
                                        }
                                        double width = (rate * loadPane.getWidth()) / 100;
                                        loadsPane.setMaxWidth(width);
                                        loadPane.widthProperty().addListener(new ChangeListener<Number>() {

                                            @Override
                                            public void changed(ObservableValue<? extends Number> unknown,
                                                    Number oldNumber, Number currentNumber) {
                                                double sum = (rate * currentNumber.doubleValue()) / 100;
                                                loadsPane.setMaxWidth(sum);
                                            }

                                        });
                                    }

                                });
                            }
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Scanner getScanner = new Scanner(new File("src/Systems/history/boolean.txt"))
                                    .useDelimiter("\\Z");
                            Boolean getBoolean = getScanner.nextBoolean();

                            String addString = ",";
                            if (getBoolean != true) {
                                addString = "";

                                PrintWriter writer = new PrintWriter(new File("src/Systems/history/boolean.txt"));
                                writer.print("true");
                                writer.close();
                            }

                            Scanner scanner = new Scanner(new File("src/Systems/history/content.txt"))
                                    .useDelimiter("\\Z");
                            String content = scanner.next();
                            content = content.substring(0, content.length() - 1);

                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            String time = timestamp.toString();

                            JSONObject object = new JSONObject();
                            object.put("name", name);
                            object.put("file", strings[0]);
                            object.put("size", strings[1]);
                            object.put("status", "Sent");
                            object.put("time", time);
                            object.put("location", strings[2]);

                            content = content + addString + object.toJSONString() + "]";

                            PrintWriter writer = new PrintWriter(new File("src/Systems/history/content.txt"));
                            writer.print(content);
                            writer.close();

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    booleanProperty.set(!booleanProperty.get());
                                }

                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.print(e + ": line 956");

                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            LABEL.setText("Disconnected");
                            LABEL.setTextFill(Color.rgb(194, 57, 84));
                        }

                    });

                }
            }

        });
        thread.start();
    }

    // File Box

    private void fileUIX(VBox vBox, StackPane loadPane, StackPane loadsPane, String[] strings, Label[] labels) {
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(10));
        stackPane.setCursor(Cursor.HAND);
        stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.print(strings[2]);
                try {
                    Runtime.getRuntime().exec("explorer /select, " + strings[2]);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }

        });
        vBox.getChildren().addAll(stackPane);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        stackPane.getChildren().addAll(hBox);

        // Logo

        StackPane imgPane = new StackPane();
        hBox.getChildren().addAll(imgPane);

        ImageView imageView = new ImageView(new Image("files/icons/file.png"));
        imageView.setFitHeight(70);
        imageView.setFitWidth(70);
        imgPane.getChildren().addAll(imageView);

        // Content

        StackPane contPane = new StackPane();
        HBox.setHgrow(contPane, Priority.ALWAYS);
        hBox.getChildren().addAll(contPane);

        VBox contBox = new VBox();
        contBox.setSpacing(5);
        contPane.getChildren().addAll(contBox);

        // Names

        HBox nameBox = new HBox();
        nameBox.setSpacing(20);
        nameBox.setAlignment(Pos.CENTER);
        contBox.getChildren().addAll(nameBox);

        Label label = new Label(strings[0]);
        label.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        label.setTextFill(Color.rgb(30, 30, 30));
        nameBox.getChildren().addAll(label);

        StackPane statusPane = new StackPane();
        statusPane.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(statusPane, Priority.ALWAYS);
        nameBox.getChildren().addAll(statusPane);

        labels[0].setFont(Font.font("", 14));
        labels[0].setTextFill(Color.DARKGRAY);
        statusPane.getChildren().addAll(labels[0]);

        // Loading

        loadPane.setMaxHeight(14);
        loadPane.setMinHeight(14);
        loadPane.setAlignment(Pos.CENTER_LEFT);
        loadPane.setStyle("-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: rgb(220,220,220);");
        contBox.getChildren().addAll(loadPane);

        loadsPane.setMaxWidth(0);
        loadsPane
                .setStyle("-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: rgb(20, 212, 20);");
        loadPane.getChildren().addAll(loadsPane);

        // Details

        HBox bottomBox = new HBox();
        bottomBox.setSpacing(20);
        bottomBox.setAlignment(Pos.CENTER);
        contBox.getChildren().addAll(bottomBox);

        Label sizeLabel = new Label(strings[1]);
        sizeLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        sizeLabel.setTextFill(Color.GRAY);
        bottomBox.getChildren().addAll(sizeLabel);

        StackPane ratePane = new StackPane();
        ratePane.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(ratePane, Priority.ALWAYS);
        bottomBox.getChildren().addAll(ratePane);

        labels[1].setFont(Font.font("", 14));
        labels[1].setTextFill(Color.GRAY);
        ratePane.getChildren().addAll(labels[1]);
    }

    // History

    private void historyPage(Tab tab) {

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: white;");
        tab.setContent(stackPane);

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        stackPane.getChildren().addAll(vBox);

        // Heading

        HBox topBox = new HBox();
        topBox.setSpacing(20);
        topBox.setPadding(new Insets(40));
        topBox.setStyle("-fx-background-color: whitesmoke;");
        vBox.getChildren().addAll(topBox);

        VBox nameBox = new VBox();
        nameBox.setSpacing(4);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        topBox.getChildren().addAll(nameBox);

        Label nameLabel = new Label("History");
        nameLabel.setFont(Font.font("recaleto", 25));
        nameLabel.setTextFill(Color.STEELBLUE);
        nameBox.getChildren().addAll(nameLabel);

        nameLabel = new Label("List of all transfered files");
        nameLabel.setFont(Font.font("", 15));
        nameLabel.setTextFill(Color.GRAY);
        nameBox.getChildren().addAll(nameLabel);

        Button headingButton = new Button("Clear All");
        headingButton.setPrefSize(150, 37);
        headingButton.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        headingButton.setTextFill(Color.WHITE);
        headingButton
                .setStyle("-fx-border-radius: 30; -fx-background-radius: 30; -fx-background-color: rgb(30, 179, 238);");
        headingButton.setCursor(Cursor.HAND);
        headingButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub

                StackPane boxPane = new StackPane();
                boxPane.setMaxSize(380, 300);
                boxPane.setPadding(new Insets(20, 30, 20, 30));
                boxPane.setStyle("-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");

                loadingPane.setVisible(true);
                loadingPane.getChildren().addAll(boxPane);

                VBox boxVBox = new VBox();
                boxVBox.setSpacing(20);
                boxVBox.setAlignment(Pos.CENTER);
                boxPane.getChildren().addAll(boxVBox);

                StackPane imgPane = new StackPane();
                boxVBox.getChildren().addAll(imgPane);

                ImageView imageView = new ImageView(new Image("files/images/trash.gif"));
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imgPane.getChildren().addAll(imageView);

                StackPane labelPane = new StackPane();
                labelPane.setAlignment(Pos.CENTER);
                boxVBox.getChildren().addAll(labelPane);

                String content = "You are about to clean / remove history permanently. Do you want to continue ?";

                Label label = new Label(content);
                label.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
                label.setTextFill(Color.GRAY);
                label.setWrapText(true);
                label.setTextAlignment(TextAlignment.CENTER);
                labelPane.getChildren().addAll(label);

                HBox hBox = new HBox();
                hBox.setSpacing(10);
                boxVBox.getChildren().addAll(hBox);

                Button button = new Button("Clear");
                button.setPrefSize(200, 34);
                button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
                button.setTextFill(Color.WHITE);
                button.setStyle("-fx-background-color: dodgerblue;");
                button.setCursor(Cursor.HAND);
                button.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {

                        loadingPane.getChildren().clear();
                        loadingPane.setVisible(false);

                        try {
                            PrintWriter writer = new PrintWriter(new File("src/Systems/history/content.txt"));
                            writer.print("[]");
                            writer.close();

                            PrintWriter printWriter = new PrintWriter(new File("src/Systems/history/boolean.txt"));
                            printWriter.print("false");
                            printWriter.close();
                        } catch (Exception e) {
                            System.out.print(e);
                        }

                        historyVBox.getChildren().clear();
                        historyLoads();
                    }

                });
                hBox.getChildren().addAll(button);

                button = new Button("Cancel");
                button.setPrefSize(200, 34);
                button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
                button.setTextFill(Color.BLACK);
                button.setStyle("-fx-background-color: rgb(200,200,200);");
                button.setCursor(Cursor.HAND);
                button.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        loadingPane.getChildren().clear();
                        loadingPane.setVisible(false);
                    }

                });
                hBox.getChildren().addAll(button);
            }

        });
        topBox.getChildren().addAll(headingButton);

        // Content

        StackPane contPane = new StackPane();
        VBox.setVgrow(contPane, Priority.ALWAYS);
        vBox.getChildren().addAll(contPane);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.getStyleClass().addAll("scroll-pane");
        contPane.getChildren().addAll(scrollPane);

        StackPane contentPane = new StackPane();
        contentPane.setPadding(new Insets(20, 40, 20, 40));
        scrollPane.setContent(contentPane);

        historyVBox = new VBox();
        contentPane.getChildren().addAll(historyVBox);

        // Data

        historyLoads();

        // Boolean Property

        booleanProperty = new SimpleBooleanProperty();
        booleanProperty.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> unknown, Boolean old, Boolean current) {
                historyVBox.getChildren().clear();
                historyLoads();
            }

        });

    }

    // History : Extend

    private void historyLoads() {
        //

        VBox vBox = new VBox();
        historyVBox.getChildren().addAll(vBox);

        try {
            // Get Content

            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new FileReader(new File("src/Systems/history/content.txt")));

            LocalDate date = LocalDate.now();
            String getDate = "";
            Boolean getBoolean = true;
            VBox contBox = null;

            for (Object object : array) {
                JSONObject jsonObject = (JSONObject) object;

                String value0 = (String) jsonObject.get("name");
                String value1 = (String) jsonObject.get("file");
                String value2 = (String) jsonObject.get("size");
                String value3 = (String) jsonObject.get("status");
                String value4 = (String) jsonObject.get("time");
                String value5 = (String) jsonObject.get("location");

                Timestamp timestamp = Timestamp.valueOf(value4);
                LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                String dateString = localDate.toString();

                if (!dateString.equals(getDate)) {
                    VBox box = new VBox();
                    vBox.getChildren().add(0, box);
                    contBox = new VBox();
                    historyContBox(value4, box, contBox);
                    getDate = dateString;
                    getBoolean = true;
                }

                String[] strings = { value1, value0, value2, value3, value4, value5 };
                historyList(contBox, getBoolean, strings);
                getBoolean = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e);
        }

    }

    // History Box

    private void historyContBox(String getDate, VBox box, VBox vBox) {
        Timestamp timestamp = Timestamp.valueOf(getDate);
        LocalDate date = timestamp.toLocalDateTime().toLocalDate();
        String dateString0 = date.toString();

        Instant instant = Instant.now().minus(1, ChronoUnit.DAYS);
        LocalDate yesterday = LocalDate.ofInstant(instant, ZoneId.systemDefault());
        LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
        String dateString1 = yesterday.toString();
        String dateString2 = today.toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE, dd MM yyyy");
        String dateString = formatter.format(date);

        if (dateString0.equals(dateString2)) {
            dateString = "Today";
        }
        if (dateString0.equals(dateString1)) {
            dateString = "Yesterday";
        }

        box.setSpacing(15);
        box.setPadding(new Insets(0, 40, 30, 40));

        Label label = new Label(dateString);
        label.setFont(Font.font("sans-serif", FontWeight.BOLD, 16));
        label.setTextFill(Color.rgb(18, 52, 86));
        box.getChildren().addAll(label);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(0, 20, 0, 20));
        stackPane.setEffect(new DropShadow(4, Color.DARKGRAY));
        stackPane.setStyle("-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;");
        box.getChildren().addAll(stackPane);

        stackPane.getChildren().addAll(vBox);

    }

    // History List

    private void historyList(VBox vBox, Boolean getBoolean, String[] strings) {

        HBox hBox = new HBox();
        hBox.setSpacing(30);
        hBox.setPadding(new Insets(18, 0, 18, 0));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(0, hBox);

        if (getBoolean != true) {
            hBox.setStyle("-fx-border-width: 0 0 1.5 0; -fx-border-color: rgb(240,240,240);");
        }

        Timestamp timestamp = Timestamp.valueOf(strings[4]);
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat format = new SimpleDateFormat("EE, h:m a ");
        strings[4] = format.format(date);

        int num = 0;
        while (num != 6) {

            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(stackPane);
            if (num == 0) {
                HBox.setHgrow(stackPane, Priority.ALWAYS);
            }

            if (num != 5) {
                Label label = new Label(strings[num]);
                label.setFont(Font.font("", 16));
                label.setTextFill(Color.rgb(100, 100, 100));
                stackPane.getChildren().addAll(label);
            } else {
                String style = "-fx-border-radius: 30; -fx-background-radius: 30; -fx-background-color: steelblue;";

                Button button = new Button("Open");
                button.setPrefSize(120, 30);
                button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
                button.setTextFill(Color.WHITE);
                button.setStyle(style);
                button.setCursor(Cursor.HAND);
                stackPane.getChildren().addAll(button);
                button.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        // TODO Auto-generated method stub
                        try {
                            Runtime.getRuntime().exec("explorer /select, " + strings[5] + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.print(e);
                        }
                    }

                });
            }

            num++;

        }
    }

    // How To Use Page

    private void learnPage(Tab tab) {

        StackPane stackPane = new StackPane();
        tab.setContent(stackPane);

        WebView webView = new WebView();
        // webView.set
        stackPane.getChildren().add(webView);

        try {

            File file = new File("src/Systems/web/index.html");
            WebEngine engine = webView.getEngine();
            engine.load(file.toURI().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // About

    private void aboutPage(Tab tab) {

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(40));
        stackPane.setStyle("-fx-background-color: rgb(250,250,250);");
        tab.setContent(stackPane);

        VBox vBox = new VBox();
        vBox.setSpacing(30);
        stackPane.getChildren().add(vBox);

        // Heading

        StackPane headPane = new StackPane();
        headPane.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().add(headPane);

        Label label = new Label("About");
        label.setFont(Font.font("arial rounded MT bold", 30));
        headPane.getChildren().add(label);

        // Content

        StackPane contPane = new StackPane();
        contPane.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().add(contPane);

        String content = "FileByte is a free and open source software used for sharing files between devices by using local network. It was developed using java programming language and it use JavaFX as Graphic User Interface (GUI). It was made as small project on web sockets in Java Networking. It is working under BaseByte Cooperation. The earliest version was developed in 2022.";

        Label contLabel = new Label(content);
        contLabel.setFont(Font.font("Recoleta,Noto Serif,Georgia,Times New Roman,Times,serif;", 16));
        contLabel.setTextFill(Color.rgb(100, 100, 100));
        contLabel.setWrapText(true);
        contPane.getChildren().add(contLabel);

        // Details

        VBox box = new VBox();
        box.setSpacing(2);
        vBox.getChildren().add(box);

        aboutExtend(box, "Version", "1.0.0");
        aboutExtend(box, "Developer", "Lyman Ken Mpharo");
        aboutExtend(box, "Cooperation", "BaseByte");
        aboutExtend(box, "Website", "www.filebyte.com");

        // Button

        StackPane buttonPane = new StackPane();
        buttonPane.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().add(buttonPane);

        Button button = new Button("Visit Web");
        button.setPrefSize(150, 37);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: steelblue;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("rundll32 url.dll,FileProtocolHandler https://www.filebyte.com");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        buttonPane.getChildren().add(button);

        // Logo

        StackPane logoPane = new StackPane();
        VBox.setVgrow(logoPane, Priority.ALWAYS);
        vBox.getChildren().add(logoPane);

        ImageView imageView = new ImageView(new Image("files/logo/logo.png"));
        imageView.setFitHeight(170);
        imageView.setFitWidth(170);
        logoPane.getChildren().add(imageView);

    }

    // About : Extend

    private void aboutExtend(VBox vBox, String name, String value) {

        HBox hBox = new HBox();
        hBox.setSpacing(20);
        vBox.getChildren().add(hBox);

        // Name

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.setMinWidth(200);
        hBox.getChildren().add(pane);

        Label label = new Label(name);
        label.setFont(Font.font("recaleto", 16));
        pane.getChildren().add(label);

        // Value

        pane = new StackPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(pane);

        label = new Label(value);
        label.setFont(Font.font("recaleto", 16));
        label.setTextFill(Color.DARKGRAY);
        pane.getChildren().add(label);

    }

    // Settings

    private void settingsPage(Tab tab) {

        StackPane stackPane = new StackPane();
        tab.setContent(stackPane);

        StackPane pane = new StackPane();
        pane.setPadding(new Insets(20, 100, 20, 100));
        stackPane.getChildren().addAll(pane);

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        pane.getChildren().addAll(vBox);

        // Heading

        StackPane headingPane = new StackPane();
        headingPane.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(headingPane);

        Label label = new Label("Settings");
        label.setFont(Font.font("Arial Rounded MT Bold", 30));
        headingPane.getChildren().addAll(label);

        // Account

        VBox accountVBox = new VBox();
        accountVBox.setSpacing(10);
        accountVBox.setPadding(new Insets(0, 0, 20, 0));
        accountVBox.setStyle("-fx-border-width: 0 0 1.4 0; -fx-border-color: rgb(230,230,230);");
        vBox.getChildren().addAll(accountVBox);

        Label accountLabel = new Label("Account");
        accountLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        accountLabel.setTextFill(Color.GRAY);
        accountVBox.getChildren().addAll(accountLabel);

        HBox accountBox = new HBox();
        accountBox.setSpacing(10);
        accountBox.setAlignment(Pos.CENTER_LEFT);
        accountVBox.getChildren().addAll(accountBox);

        String getString = AppName.substring(0, 1).toUpperCase();
        char logoChar = getString.charAt(0);

        Button logoButton = new Button(logoChar + "");
        logoButton.setPadding(new Insets(0));
        logoButton.setMaxSize(45, 45);
        logoButton.setMinSize(45, 45);
        logoButton.setFont(Font.font("Sans-serif", FontWeight.BOLD, 16));
        logoButton.setTextFill(Color.WHITE);
        logoButton.setStyle("-fx-border-radius: 45; -fx-background-radius: 45; -fx-background-color: brown;");
        accountBox.getChildren().addAll(logoButton);

        Label nameLabel = new Label(AppName);
        nameLabel.setFont(Font.font("", 15));
        accountBox.getChildren().addAll(nameLabel);

        StackPane buttonPane = new StackPane();
        buttonPane.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(buttonPane, Priority.ALWAYS);
        accountBox.getChildren().addAll(buttonPane);

        Button button = new Button("Change");
        button.setPrefSize(90, 30);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.DEEPSKYBLUE);
        button.setEffect(new DropShadow(3, Color.GRAY));
        button.setStyle("-fx-background-color: white;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // Get Popup

                StackPane onPane = new StackPane();

                TextField textField = new TextField();
                textField.setPrefHeight(37);
                textField.setPromptText("New Name");
                textField.setAlignment(Pos.CENTER);
                textField.setStyle(
                        "-fx-border-width: 1.6; -fx-border-color: rgb(200,200,200); -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: white;");
                onPane.getChildren().addAll(textField);

                Button cancelButton = new Button();

                Button proceedButton = new Button("Change");
                proceedButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        String name = textField.getText();
                        if (!name.equals("")) {
                            name = name.substring(0, 1).toUpperCase() + name.substring(1);
                            AppName = name;
                            nameLabel.setText(name);

                            char getLogo = name.charAt(0);
                            logoButton.setText(getLogo + "");

                            try {
                                PrintWriter writer = new PrintWriter(new File("src/Systems/settings/AppName/name.txt"));
                                writer.print(name);
                                writer.close();
                            } catch (Exception e) {
                                System.out.print(e);
                            }

                            loadingPane.getChildren().clear();
                            loadingPane.setVisible(false);
                        }
                    }

                });

                Button[] buttons = { cancelButton, proceedButton };

                String[] strings = { "Name", "Your input name will be displayed on receiver's or sender's device" };
                settingsExtend(onPane, buttons, strings);
            }

        });
        buttonPane.getChildren().addAll(button);

        // Destination

        VBox locationBox = new VBox();
        locationBox.setSpacing(20);
        locationBox.setPadding(new Insets(0, 0, 25, 0));
        locationBox.setStyle("-fx-border-width: 0 0 1.4 0; -fx-border-color: rgb(230,230,230);");
        vBox.getChildren().addAll(locationBox);

        Label locationLabel = new Label("Destination");
        locationLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        locationLabel.setTextFill(Color.GRAY);
        locationBox.getChildren().addAll(locationLabel);

        HBox destBox = new HBox();
        destBox.setSpacing(20);
        destBox.setAlignment(Pos.CENTER_LEFT);
        locationBox.getChildren().addAll(destBox);

        Label destLabel = new Label(Destination);
        destLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));

        button = new Button("Change");
        button.setPrefSize(90, 30);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.GRAY);
        button.setEffect(new DropShadow(3, Color.GRAY));
        button.setStyle("-fx-background-color: white;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                File file = chooser.showDialog(new Stage());

                if (file != null) {
                    Destination = file.getAbsolutePath();
                    destLabel.setText(Destination);

                    try {
                        PrintWriter writer = new PrintWriter(new File("src/Systems/settings/Destination/location.txt"));
                        writer.print(Destination);
                        writer.close();
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.print(Destination);
                    }
                }
            }

        });

        destBox.getChildren().addAll(button, destLabel);

        // Connection

        VBox connBox = new VBox();
        connBox.setSpacing(20);
        connBox.setPadding(new Insets(0, 0, 30, 0));
        connBox.setStyle("-fx-border-width: 0 0 1.4 0; -fx-border-color: rgb(230,230,230);");
        vBox.getChildren().addAll(connBox);

        Label connLabel = new Label("Connections");
        connLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        connLabel.setTextFill(Color.GRAY);
        connBox.getChildren().addAll(connLabel);

        VBox connVBox = new VBox();
        connVBox.setSpacing(20);
        connBox.getChildren().addAll(connVBox);

        // Ports

        VBox portBox = new VBox();
        portBox.setSpacing(7);
        connVBox.getChildren().addAll(portBox);

        Label portLabel = new Label("PORT");
        portLabel.setFont(Font.font("recaleto", FontWeight.BOLD, 16));
        portLabel.setTextFill(Color.DARKGRAY);
        portBox.getChildren().addAll(portLabel);

        HBox portHBox = new HBox();
        portHBox.setSpacing(10);
        portBox.getChildren().addAll(portHBox);

        VBox portVBox = new VBox();
        portVBox.setSpacing(3);
        HBox.setHgrow(portVBox, Priority.ALWAYS);
        portHBox.getChildren().addAll(portVBox);

        Label numberLabel = new Label("Port Number");
        numberLabel.setFont(Font.font("recaleto", 15));

        Label valueLabel = new Label(Port + "");
        valueLabel.setFont(Font.font("recaleto", 15));
        valueLabel.setTextFill(Color.GRAY);

        portVBox.getChildren().addAll(numberLabel, valueLabel);

        button = new Button("Change");
        button.setPrefSize(90, 30);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.GRAY);
        button.setEffect(new DropShadow(3, Color.GRAY));
        button.setStyle("-fx-background-color: white;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                StackPane onPane = new StackPane();

                String[] strings = { "Port", "Make sure all devices have been set to the same port to connect" };
                String[] ports = { "3001", "1", "2", "3", "4", "7", "14", "900", "3000", "3333", "4444" };

                ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(ports));
                choiceBox.setPrefWidth(400);
                choiceBox.setPrefHeight(37);
                choiceBox.setValue(Port + "");
                choiceBox.setStyle("-fx-background-color: whitesmoke;");
                choiceBox.getStyleClass().addAll("get-box");
                onPane.getChildren().addAll(choiceBox);

                Button cancelButton = new Button();

                Button proceedButton = new Button("Change");
                proceedButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent actionEvent) {
                        String getPort = choiceBox.getValue();
                        Port = Integer.parseInt(getPort);
                        valueLabel.setText(getPort);

                        try {
                            PrintWriter writer = new PrintWriter(new File("src/Systems/settings/Connection/port.txt"));
                            writer.print(getPort);
                            writer.close();
                        } catch (Exception e) {
                            System.out.print(e);
                        }

                        loadingPane.getChildren().clear();
                        loadingPane.setVisible(false);
                    }

                });

                Button[] buttons = { cancelButton, proceedButton };

                settingsExtend(onPane, buttons, strings);

            }

        });
        portHBox.getChildren().addAll(button);

        // Method

        VBox typeBox = new VBox();
        typeBox.setSpacing(10);
        connVBox.getChildren().addAll(typeBox);

        Label typeLabel = new Label("METHOD");
        typeLabel.setFont(Font.font("recaleto", FontWeight.BOLD, 15));
        typeLabel.setTextFill(Color.DARKGRAY);
        typeBox.getChildren().addAll(typeLabel);

        // Wifi

        HBox typeHBox = new HBox();
        typeHBox.setSpacing(10);
        typeHBox.setPadding(new Insets(5, 0, 0, 0));
        typeBox.getChildren().addAll(typeHBox);

        Label getLabel = new Label("Wifi");
        getLabel.setFont(Font.font("", 15));
        getLabel.setPrefWidth(250);
        typeHBox.getChildren().addAll(getLabel);

        CheckBox wifiBox = new CheckBox();
        wifiBox.setSelected(Wifi);
        wifiBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                Boolean getWifi = wifiBox.isSelected();
                Wifi = getWifi;

                try {
                    PrintWriter writer = new PrintWriter(new File("src/Systems/settings/Connection/wifi.txt"));
                    writer.print(getWifi);
                    writer.close();
                } catch (Exception e) {
                    System.out.print(e);
                }
            }

        });
        typeHBox.getChildren().addAll(wifiBox);

        // Ethernet

        typeHBox = new HBox();
        typeHBox.setSpacing(10);
        typeBox.getChildren().addAll(typeHBox);

        getLabel = new Label("Ethernet");
        getLabel.setFont(Font.font("", 15));
        getLabel.setPrefWidth(250);
        typeHBox.getChildren().addAll(getLabel);

        CheckBox ethernetBox = new CheckBox();
        ethernetBox.setSelected(Ethernet);
        ethernetBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                Boolean getEthernet = ethernetBox.isSelected();
                Ethernet = getEthernet;

                try {
                    PrintWriter writer = new PrintWriter(new File("src/Systems/settings/Connection/ethernet.txt"));
                    writer.print(getEthernet);
                    writer.close();
                } catch (Exception e) {
                    System.out.print(e);
                }
            }

        });
        typeHBox.getChildren().addAll(ethernetBox);

        // Restore

        StackPane restorePane = new StackPane();
        restorePane.setPadding(new Insets(20, 0, 20, 0));
        restorePane.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(restorePane);

        button = new Button("Restore Default");
        button.setPrefSize(200, 45);
        button.setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: dodgerblue;");
        button.setCursor(Cursor.HAND);
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub

                String[] content = { "Restore",
                        "Restoring settings to Default will change all new settings you set to old ones. Do you want to continue restoring" };

                Button cancelButton = new Button();
                Button restoreButton = new Button("Restore");
                restoreButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent actionEvent) {

                        try {

                            // Name

                            PrintWriter writer = new PrintWriter(new File("src/Systems/settings/AppName/name.txt"));
                            writer.print(System.getProperty("user.name"));
                            writer.close();

                            AppName = System.getProperty("user.name");
                            nameLabel.setText(AppName);

                            char logos = AppName.substring(0, 1).toUpperCase().charAt(0);
                            logoButton.setText(logos + "");

                            // Location

                            writer = new PrintWriter(new File("src/Systems/settings/Destination/location.txt"));
                            writer.print(new File("C:/Users/lyman/Desktop").getAbsolutePath());
                            writer.close();

                            Destination = new File("C:/Users/lyman/Desktop").getAbsolutePath();
                            destLabel.setText(Destination);

                            // Port

                            writer = new PrintWriter(new File("src/Systems/settings/Connection/port.txt"));
                            writer.print("3001");
                            writer.close();

                            Port = 3001;
                            valueLabel.setText("3001");

                            // Wifi

                            writer = new PrintWriter(new File("src/Systems/settings/Connection/wifi.txt"));
                            writer.print("true");
                            writer.close();

                            Wifi = true;
                            wifiBox.setSelected(true);

                            // Ethernet

                            writer = new PrintWriter(new File("src/Systems/settings/Connection/ethernet.txt"));
                            writer.print("true");
                            writer.close();

                            Ethernet = true;
                            ethernetBox.setSelected(true);

                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                        loadingPane.getChildren().clear();
                        loadingPane.setVisible(false);

                    }

                });

                Button[] buttons = { cancelButton, restoreButton };
                settingsExtend(new StackPane(), buttons, content);

            }

        });
        restorePane.getChildren().addAll(button);

    }

    // Settings Extend

    private void settingsExtend(StackPane pane, Button[] buttons, String[] strings) {

        StackPane stackPane = new StackPane();
        stackPane.setMaxSize(350, 280);
        stackPane.setPadding(new Insets(20));
        stackPane.setStyle("-fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: white;");

        loadingPane.setVisible(true);
        loadingPane.getChildren().addAll(stackPane);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(15);
        stackPane.getChildren().addAll(vBox);

        // TItle

        StackPane headingPane = new StackPane();
        vBox.getChildren().addAll(headingPane);

        Label headingLabel = new Label(strings[0]);
        headingLabel.setFont(Font.font("recaleto", 30));
        headingLabel.setTextFill(Color.GRAY);
        headingPane.getChildren().addAll(headingLabel);

        // Content

        StackPane contPane = new StackPane();
        vBox.getChildren().addAll(contPane);

        Label contLabel = new Label(strings[1]);
        contLabel.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));
        contLabel.setAlignment(Pos.CENTER);
        contLabel.setTextAlignment(TextAlignment.CENTER);
        contLabel.setWrapText(true);
        contPane.getChildren().addAll(contLabel);

        // Panes

        vBox.getChildren().addAll(pane);

        // Buttons

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        vBox.getChildren().addAll(hBox);

        buttons[0].setText("Cancel");
        buttons[0].setPrefSize(200, 34);
        buttons[0].setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        buttons[0].setTextFill(Color.GRAY);
        buttons[0].setEffect(new DropShadow(3, Color.rgb(200, 200, 200)));
        buttons[0].setStyle("-fx-background-color: white;");
        buttons[0].setCursor(Cursor.HAND);
        buttons[0].setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                loadingPane.getChildren().clear();
                loadingPane.setVisible(false);
            }

        });

        buttons[1].setPrefSize(200, 34);
        buttons[1].setFont(Font.font("sans-serif", FontWeight.BOLD, 14));
        buttons[1].setTextFill(Color.WHITE);
        buttons[1].setStyle("-fx-background-color: Dodgerblue;");
        buttons[1].setCursor(Cursor.HAND);

        hBox.getChildren().addAll(buttons[0], buttons[1]);

    }

}
