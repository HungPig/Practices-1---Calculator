/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package thuchanh1;

import com.formdev.flatlaf.FlatDarkLaf;
import java.math.BigDecimal;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class Calculator extends javax.swing.JFrame {

    private BigDecimal currentValue = BigDecimal.ZERO;
    private BigDecimal savedValue = BigDecimal.ZERO;
    private Connection connection;
    private boolean initValue = true;
    private boolean doInitValue = true;
    private char commandCode = '=';
    BigDecimal memoryValue = BigDecimal.ZERO;
    private String text;
    private String topText = "";
    private final String template
            = "<html>"
            + "  <head>"
            + "  </head>"
            + "  <body>"
            + "    <p style=\"text-align:right;font-size:10px;margin-top: 0\">"
            + "     %s"
            + "    </p>"
            + "    <p style=\"text-align:right;font-size:14px;margin-top: 0\">"
            + "     %s"
            + "    </p>"
            + "  </body>"
            + "</html>";

    /**
     * Creates new form Calculator
     */
    public Calculator() {
        initComponents();
        jPanel1.setVisible(false);
        setSize(new java.awt.Dimension(jPanel1.getWidth(), getHeight()));
        final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        initCalc();
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:calculator.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, expression TEXT, result TEXT)");
        } catch (SQLException e) {
//            
        }
    }

    private void setText(String text) {
        this.text = text;
        jTextField1.setText(String.format(template, topText, text));
    }

    private String getText() {
        return text;
    }

    private void setTopText(String topText) {
        this.topText += this.topText.equals("") ? topText : (" " + topText);
        setText(getText());
    }

    private void addCalc(java.awt.event.ActionEvent evt) {
        if (initValue) {
            if (evt.getActionCommand().equals(",")) {
                setText("0" + evt.getActionCommand());
            } else {
                setText(evt.getActionCommand());
            }
        } else {
            setText(getText() + evt.getActionCommand());
        }
        if (commandCode == '=') {
            savedValue = new BigDecimal(getText().replace(',', '.'));
            currentValue = BigDecimal.ZERO;
        } else {
            currentValue = new BigDecimal(getText().replace(',', '.'));
        }
        initValue = false;
    }

    private void initCalc() {
        currentValue = BigDecimal.ZERO;
        savedValue = BigDecimal.ZERO;
        initValue = true;
        doInitValue = true;
        commandCode = '=';
        topText = "";
        setText("0");
    }

    private void fCalc(String command) {
        if (null != command) {
            switch (command) {
                case "ce" ->
                    initCalc();
                case "=" -> {
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = calcResult(value);
                        commandCode = '=';
                        this.topText = "";
                        setText(result.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                                .replaceFirst("0+$", "").replaceFirst(",$", ""));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                }
                case "+-" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    currentValue = currentValue.multiply(new BigDecimal("-1"));
                    setText(currentValue.toString().replace('.', ','));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = false;
                }
                case "sqrt" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        currentValue = BigDecimalUtil.sqrt(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "sqr" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = currentValue.pow(2);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "ln" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.compareTo(BigDecimal.ZERO) < 0
                                || currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.ln(currentValue, 32);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "log" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.compareTo(BigDecimal.ZERO) < 0
                                || currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.log10(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "sin" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    switch (buttonGroup1.getSelection().getMnemonic()) {
                        case 'D' ->
                            currentValue = currentValue.multiply(BigDecimalUtil.PI_DIV_180);
                        case 'G' ->
                            currentValue = currentValue.multiply(BigDecimalUtil.PI_DIV_200);
                        default -> {
                        }
                    }
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.sine(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "cos" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    switch (buttonGroup1.getSelection().getMnemonic()) {
                        case 'D' ->
                            currentValue = currentValue.multiply(BigDecimalUtil.PI_DIV_180);
                        case 'G' ->
                            currentValue = currentValue.multiply(BigDecimalUtil.PI_DIV_200);
                        default -> {
                        }
                    }
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.cosine(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "tan" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    switch (buttonGroup1.getSelection().getMnemonic()) {
                        case 'D' ->
                            currentValue = currentValue.multiply(BigDecimalUtil.PI_DIV_180);
                        case 'G' ->
                            currentValue = currentValue.multiply(BigDecimalUtil.PI_DIV_200);
                        default -> {
                        }
                    }
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.tangent(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "cube" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = currentValue.pow(3);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "cuberoot" -> {
                    currentValue = savedValue == BigDecimal.ZERO
                            ? new BigDecimal(getText().replace(',', '.')) : savedValue;
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.cuberoot(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "pow" -> {
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = BigDecimalUtil.pow(savedValue, value);
                        setText(result.toString().replace('.', ','));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                    commandCode = '^';
                    setText(getText() + " " + commandCode);
                }
                case "yroot" -> {
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = BigDecimalUtil.pow(savedValue, BigDecimal.ONE.divide(value, 32, BigDecimal.ROUND_HALF_UP));
                        setText(result.toString().replace('.', ','));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                    commandCode = 'r';
                    setTopText(getText() + " " + commandCode);
                }
                case "arcsin" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.asin(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    switch (buttonGroup1.getSelection().getMnemonic()) {
                        case 'D' ->
                            currentValue = currentValue.divide(BigDecimalUtil.PI_DIV_180, 32, BigDecimal.ROUND_HALF_UP);
                        case 'G' ->
                            currentValue = currentValue.divide(BigDecimalUtil.PI_DIV_200, 32, BigDecimal.ROUND_HALF_UP);
                        default -> {
                        }
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "arccos" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.acos(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    switch (buttonGroup1.getSelection().getMnemonic()) {
                        case 'D' ->
                            currentValue = currentValue.divide(BigDecimalUtil.PI_DIV_180, 32, BigDecimal.ROUND_HALF_UP);
                        case 'G' ->
                            currentValue = currentValue.divide(BigDecimalUtil.PI_DIV_200, 32, BigDecimal.ROUND_HALF_UP);
                        default -> {
                        }
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "arctan" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    try {
                        if (currentValue.toBigInteger().toString().length() > 256) {
                            initCalc();
                            setText("Error.");
                            return;
                        }
                        currentValue = BigDecimalUtil.atan(currentValue);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    switch (buttonGroup1.getSelection().getMnemonic()) {
                        case 'D':
                            currentValue = currentValue.divide(BigDecimalUtil.PI_DIV_180, 32, BigDecimal.ROUND_HALF_UP);
                            break;
                        case 'G':
                            currentValue = currentValue.divide(BigDecimalUtil.PI_DIV_200, 32, BigDecimal.ROUND_HALF_UP);
                            break;
                        default:
                            break;
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "nbs" -> {
                    if (!initValue && getText().matches("[\\d,]+")) {
                        if (getText().length() == 1) {
                            setText("0");
                            initValue = true;
                        } else {
                            setText(getText().substring(0, getText().length() - 1));
                        }
                        if (commandCode == '=') {
                            savedValue = new BigDecimal(getText().replace(',', '.'));
                        } else {
                            currentValue = new BigDecimal(getText().replace(',', '.'));
                        }
                        return;
                    }
                }
                case "+" -> {
                    String saveText = getText();
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = calcResult(value);
                        setText(result.toString().replace('.', ','));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                    commandCode = '+';
                    setTopText(saveText + " " + commandCode);
                }
                case "-" -> {
                    String saveText = getText();
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = calcResult(value);
                        setText(result.toString().replace('.', ','));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                    commandCode = '-';
                    setTopText(saveText + " " + commandCode);
                }
                case "*" -> {
                    String saveText = getText();
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = calcResult(value);
                        setText(result.toString().replace('.', ','));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                    commandCode = '*';
                    setTopText(saveText + " " + commandCode);
                }
                case "/" -> {
                    String saveText = getText();
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = calcResult(value);
                        setText(result.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                                .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                        savedValue = result;
                        currentValue = BigDecimal.ZERO;
                    }
                    commandCode = '/';
                    setTopText(saveText + " " + commandCode);
                }
                case "1/x" -> {
                    currentValue = savedValue == BigDecimal.ZERO
                            ? new BigDecimal(getText().replace(',', '.')) : savedValue;
                    try {
                        currentValue = BigDecimal.ONE.divide(currentValue, 32, BigDecimal.ROUND_HALF_UP);
                    } catch (ArithmeticException ex) {
                        ex.getMessage();
                    }
                    setText(currentValue.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                            .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                    if (commandCode == '=') {
                        savedValue = currentValue;
                        currentValue = BigDecimal.ZERO;
                    }
                    doInitValue = true;
                }
                case "%" -> {
                    if (commandCode != '=' && !initValue) {
                        BigDecimal value = new BigDecimal(getText().replace(',', '.'));
                        BigDecimal result = savedValue.multiply(value).divide(BigDecimal.valueOf(100), 32, BigDecimal.ROUND_HALF_UP);
                        setText(result.setScale(16, BigDecimal.ROUND_HALF_UP).toPlainString().replace('.', ',')
                                .replaceFirst("(.+?)0+$", "$1").replaceFirst(",$", ""));
                        currentValue = result;
                        return;
                    }
                }
                case "MC" -> {
                    memoryValue = BigDecimal.ZERO;
                    doInitValue = true;
                }
                case "MR" -> {
                    setText(memoryValue.toPlainString().replace('.', ','));
                    if (commandCode == '=') {
                        savedValue = memoryValue;
                        currentValue = BigDecimal.ZERO;
                        doInitValue = true;
                    } else {
                        currentValue = memoryValue;
                        doInitValue = false;
                        initValue = false;
                    }
                }
                case "MS" -> {
                    memoryValue = new BigDecimal(getText().replace(',', '.'));
                    doInitValue = true;
                }
                case "M+" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    memoryValue = memoryValue.add(currentValue);
                    doInitValue = true;
                }
                case "M-" -> {
                    currentValue = new BigDecimal(getText().replace(',', '.'));
                    memoryValue = memoryValue.subtract(currentValue);
                    doInitValue = true;
                }
                default -> {
                }
            }
        }
        if (doInitValue) {
            initValue = true;
        } else {
            doInitValue = true;
        }
    }

    private void keyDetect(java.awt.event.ActionEvent evt) {
        if (evt.getActionCommand().charAt(0) >= '0' && evt.getActionCommand().charAt(0) <= '9') {
            addCalc(evt);
        } else if (evt.getActionCommand().charAt(0) == ',') {
            if (initValue || !getText().contains(",")) {
                addCalc(evt);
            }
        } else if (evt.getActionCommand().charAt(0) == '\u2190') {
            fCalc("nbs");
        }
    }

    private BigDecimal calcResult(BigDecimal value) {
        BigDecimal result = BigDecimal.ZERO;
        switch (commandCode) {
            case '+' ->
                result = savedValue.add(value);
            case '-' ->
                result = savedValue.subtract(value);
            case '*' ->
                result = savedValue.multiply(value);
            case '/' -> {
                try {
                    result = savedValue.divide(value, 32, BigDecimal.ROUND_HALF_UP);
                } catch (ArithmeticException ex) {
                    initCalc();
                    setText("Error.");
                    return result;
                }
            }
            case '^' -> {
                try {
                    result = BigDecimalUtil.pow(savedValue, value);
                } catch (ArithmeticException ex) {
                    initCalc();
                    setText("Error.");
                    return result;
                }
            }
            case 'r' -> {
                try {
                    result = BigDecimalUtil.pow(savedValue, BigDecimal.ONE.divide(value, 32, BigDecimal.ROUND_HALF_UP));
                } catch (ArithmeticException ex) {
                    initCalc();
                    setText("Error.");
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextField1 = new javax.swing.JTextPane();
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jPanel1 = new javax.swing.JPanel();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jButton56 = new javax.swing.JButton();
        jButton57 = new javax.swing.JButton();
        jButton58 = new javax.swing.JButton();
        jButton59 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calculator");
        setLocationByPlatform(true);
        setName("D"); // NOI18N
        setPreferredSize(new java.awt.Dimension(540, 295));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setContentType("text/html"); // NOI18N
        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jTextField1.setText("<html>\r\n  <head>\r\n\r  </head>\r\n  <body>\r\n    <p style=\"text-align:right;font-size:10px;margin-top: 0\">\r\n    </p>\r\n    <p style=\"text-align:right;font-size:16px;margin-top: 0\">\n     0\n    </p>\n  </body>\r\n</html>\r\n");
        jTextField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(jTextField1);

        jPanel1.setFocusable(false);

        jButton36.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton36.setText("sin");
        jButton36.setFocusable(false);
        jButton36.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton37.setText("X²");
        jButton37.setFocusable(false);
        jButton37.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton35.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton35.setText("sinh");
        jButton35.setFocusable(false);
        jButton35.setMargin(new java.awt.Insets(2, -1, 2, -1));

        jButton42.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton42.setText("Xª");
        jButton42.setFocusable(false);
        jButton42.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton41.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton41.setText("cos");
        jButton41.setFocusable(false);
        jButton41.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton40.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton40.setText("cosh");
        jButton40.setFocusable(false);
        jButton40.setMargin(new java.awt.Insets(2, -1, 2, -1));

        jButton39.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton39.setText("dms");
        jButton39.setFocusable(false);
        jButton39.setMargin(new java.awt.Insets(2, -1, 2, -1));

        jButton38.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton38.setText("n!");
        jButton38.setFocusable(false);
        jButton38.setMargin(new java.awt.Insets(2, 0, 2, 0));

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        jPanel2.setPreferredSize(new java.awt.Dimension(254, 25));

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jRadioButton4.setMnemonic('d');
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("Degrees");
        jRadioButton4.setFocusable(false);

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jRadioButton5.setMnemonic('r');
        jRadioButton5.setText("Radians");
        jRadioButton5.setFocusable(false);

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jRadioButton6.setMnemonic('g');
        jRadioButton6.setText("Grads");
        jRadioButton6.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jButton43.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton43.setText("ª√X");
        jButton43.setFocusable(false);
        jButton43.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton44.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton44.setText("π");
        jButton44.setFocusable(false);
        jButton44.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton45.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton45.setText("tanh");
        jButton45.setFocusable(false);
        jButton45.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jButton46.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton46.setText("tan");
        jButton46.setFocusable(false);
        jButton46.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        jButton47.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton47.setText("X³");
        jButton47.setFocusable(false);
        jButton47.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton48.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton48.setText("³√X");
        jButton48.setFocusable(false);
        jButton48.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        jButton49.setText(" ");
        jButton49.setEnabled(false);
        jButton49.setMargin(new java.awt.Insets(2, 0, 2, 0));

        jButton50.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton50.setText("Inv");
        jButton50.setFocusable(false);
        jButton50.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jButton51.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton51.setText("ln");
        jButton51.setFocusable(false);
        jButton51.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        jButton52.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton52.setText("(");
        jButton52.setFocusable(false);
        jButton52.setMargin(new java.awt.Insets(2, 0, 2, 0));

        jButton53.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton53.setText(")");
        jButton53.setFocusable(false);
        jButton53.setMargin(new java.awt.Insets(2, 0, 2, 0));

        jButton54.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton54.setText("Int");
        jButton54.setFocusable(false);
        jButton54.setMargin(new java.awt.Insets(2, 0, 2, 0));

        jButton55.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton55.setText("F-E");
        jButton55.setFocusable(false);
        jButton55.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton55ActionPerformed(evt);
            }
        });

        jButton56.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton56.setText("Exp");
        jButton56.setFocusable(false);
        jButton56.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton56.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton56ActionPerformed(evt);
            }
        });

        jButton57.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton57.setText("Mod");
        jButton57.setFocusable(false);
        jButton57.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton57ActionPerformed(evt);
            }
        });

        jButton58.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton58.setText("log");
        jButton58.setFocusable(false);
        jButton58.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton58ActionPerformed(evt);
            }
        });

        jButton59.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton59.setText("10ª");
        jButton59.setFocusable(false);
        jButton59.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton59.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton59ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton55, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                        .addGap(5, 5, 5)))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton49)
                    .addComponent(jButton50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton57, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton58, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton59, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        jButton29.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton29.setText("MC");
        jButton29.setFocusable(false);
        jButton29.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton2.setText("MR");
        jButton2.setFocusable(false);
        jButton2.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton3.setText("MS");
        jButton3.setFocusable(false);
        jButton3.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton4.setText("M+");
        jButton4.setFocusable(false);
        jButton4.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton5.setText("M-");
        jButton5.setFocusable(false);
        jButton5.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton6.setText("←");
        jButton6.setFocusable(false);
        jButton6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton7.setText("CE");
        jButton7.setFocusable(false);
        jButton7.setMargin(new java.awt.Insets(2, -1, 2, -1));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton8.setText("C");
        jButton8.setFocusable(false);
        jButton8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton9.setText("±");
        jButton9.setFocusable(false);
        jButton9.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton10.setText("√");
        jButton10.setFocusable(false);
        jButton10.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton15.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton15.setText("%");
        jButton15.setFocusable(false);
        jButton15.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton14.setText("/");
        jButton14.setFocusable(false);
        jButton14.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton13.setText("9");
        jButton13.setFocusable(false);
        jButton13.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton12.setText("8");
        jButton12.setFocusable(false);
        jButton12.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton11.setText("7");
        jButton11.setFocusable(false);
        jButton11.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton16.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton16.setText("4");
        jButton16.setFocusable(false);
        jButton16.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton17.setText("5");
        jButton17.setFocusable(false);
        jButton17.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton18.setText("6");
        jButton18.setFocusable(false);
        jButton18.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton19.setText("X");
        jButton19.setFocusable(false);
        jButton19.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton20.setText("1/x");
        jButton20.setFocusable(false);
        jButton20.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton25.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton25.setText("=");
        jButton25.setFocusable(false);
        jButton25.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton24.setText("-");
        jButton24.setFocusable(false);
        jButton24.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton28.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton28.setText("+");
        jButton28.setFocusable(false);
        jButton28.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton27.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton27.setText(",");
        jButton27.setFocusable(false);
        jButton27.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton23.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton23.setText("3");
        jButton23.setFocusable(false);
        jButton23.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton22.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton22.setText("2");
        jButton22.setFocusable(false);
        jButton22.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton21.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton21.setText("1");
        jButton21.setFocusable(false);
        jButton21.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton26.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton26.setText("0");
        jButton26.setFocusable(false);
        jButton26.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jMenuBar2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenu3.setMnemonic('V');
        jMenu3.setText("View");
        jMenu3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem1.setText("Standard");
        jMenuItem1.setSelected(true);
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem5.setText("Scientific");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuBar2.add(jMenu3);

        jMenu4.setMnemonic('E');
        jMenu4.setText("Edit");
        jMenu4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem2.setText("Copy");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem3.setText("Paste");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem3);

        jMenuBar2.add(jMenu4);

        jMenu1.setMnemonic('H');
        jMenu1.setText("Help");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jMenuItem4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuItem4.setText("About Calculator");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar2.add(jMenu1);

        jMenu2.setText("History");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });
        jMenuBar2.add(jMenu2);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1))))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jPanel1.setVisible(false);
        setSize(new java.awt.Dimension((int) jPanel1.getPreferredSize().getWidth() + 4, getHeight()));
        setPreferredSize(new java.awt.Dimension((int) jPanel1.getPreferredSize().getWidth() + 4, getHeight()));
        jTextField1.setSize(new java.awt.Dimension(jPanel1.getWidth(), getHeight()));
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Calculator.this.pack();
            }
        });
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jPanel1.setVisible(true);
        setSize(new java.awt.Dimension((int) jPanel1.getPreferredSize().getWidth() * 2 + 20, getHeight()));
        setPreferredSize(new java.awt.Dimension((int) jPanel1.getPreferredSize().getWidth() * 2 + 20, getHeight()));
        jTextField1.setSize(new java.awt.Dimension(jPanel1.getWidth() * 2, getHeight()));
        java.awt.EventQueue.invokeLater(Calculator.this::pack);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        java.awt.datatransfer.StringSelection data = new java.awt.datatransfer.StringSelection(getText());
        getToolkit().getSystemClipboard().setContents(data, data);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // To Do
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        About dialog = new About(this, true);
        final java.awt.Point location = getLocation();
        dialog.setLocation(location.x + 40, location.y + 40);
        dialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyChar() == '\n' || evt.getKeyChar() == '\b') {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        switch (evt.getKeyChar()) {
            case '+', '-', '*', '/', '=', '%' ->
                fCalc("" + evt.getKeyChar());
            case '\b' ->
                fCalc("nbs");
            case '\n' ->
                fCalc("=");
            default -> {
                java.awt.event.ActionEvent actionEvent = new java.awt.event.ActionEvent(this, 0, "" + evt.getKeyChar());
                if (evt.getKeyChar() == '.') {
                    actionEvent = new java.awt.event.ActionEvent(this, 0, ",");
                }
                keyDetect(actionEvent);
            }
        }
    }//GEN-LAST:event_jTextField1KeyTyped

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        fCalc(jButton50.isSelected() ? "arcsin" : "sin");
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        fCalc("sqr");
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        fCalc("pow");
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        fCalc(jButton50.isSelected() ? "arccos" : "cos");
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        fCalc("yroot");
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        addCalc(new java.awt.event.ActionEvent(this, 1, "3,1415926535897932384626433832795"));
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        fCalc(jButton50.isSelected() ? "arctan" : "tan");
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        fCalc("cube");
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        fCalc("cuberoot");
    }//GEN-LAST:event_jButton48ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        jButton50.setSelected(!jButton50.isSelected());
        jButton36.setText(jButton50.isSelected() ? "sinˉ¹" : "sin");
        jButton36.setFont(new java.awt.Font("Tahoma", 0, jButton50.isSelected() ? 8 : 11));
        jButton41.setText(jButton50.isSelected() ? "cosˉ¹" : "cos");
        jButton41.setFont(new java.awt.Font("Tahoma", 0, jButton50.isSelected() ? 8 : 11));
        jButton46.setText(jButton50.isSelected() ? "tanˉ¹" : "tan");
        jButton46.setFont(new java.awt.Font("Tahoma", 0, jButton50.isSelected() ? 8 : 11));
    }//GEN-LAST:event_jButton50ActionPerformed

    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        fCalc("ln");
    }//GEN-LAST:event_jButton51ActionPerformed

    private void jButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton55ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton55ActionPerformed

    private void jButton56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton56ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton56ActionPerformed

    private void jButton57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton57ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton57ActionPerformed

    private void jButton58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton58ActionPerformed
        fCalc("log");
    }//GEN-LAST:event_jButton58ActionPerformed

    private void jButton59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton59ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton59ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        fCalc("MC");
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        fCalc("MR");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        fCalc("MS");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        fCalc("M+");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        fCalc("M-");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        fCalc("ce");
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        fCalc("ce");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        fCalc("+-");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        fCalc("sqrt");
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        fCalc("%");
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        fCalc("/");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        fCalc("*");
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        fCalc("1/x");
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        fCalc("=");

    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        fCalc("-");
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        fCalc("+");
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        keyDetect(evt);
    }//GEN-LAST:event_jButton26ActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyTyped

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
       
    }//GEN-LAST:event_jMenu2ActionPerformed
    private void saveHistory(String expression, String result) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO history (expression, result) VALUES (?, ?)");
            preparedStatement.setString(1, expression);
            preparedStatement.setString(2, result);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatDarkLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Calculator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextField1;
    // End of variables declaration//GEN-END:variables
}
