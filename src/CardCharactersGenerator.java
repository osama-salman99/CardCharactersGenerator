import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CardCharactersGenerator extends JFrame {
    private static final String APP_NAME = "Cards Character Generator";
    private static final String PREVIEW_STRING = "A2345678910JQK";
    private static ArrayList<String> fontFamilies;

    public CardCharactersGenerator() {
        super(APP_NAME);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new GridLayout(2, 1));
        centerFrame();

        initializeFonts();

        JTextField textField = new JTextField();
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));

        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(event -> generatePreview(textField.getText()));

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(event -> generateCards(textField.getText()));

        buttonsPanel.add(previewButton);
        buttonsPanel.add(generateButton);

        add(textField);
        add(buttonsPanel);


        setVisible(true);
    }

    private void centerFrame() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - getSize().width / 2, dimension.height / 2 - getSize().height / 2);
    }

    private void initializeFonts() {
        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        fontFamilies = new ArrayList<>();
        for (Font font : allFonts) {
            String fontFamily = font.getFamily();
            if (fontFamilies.contains(fontFamily)) {
                continue;
            }
            fontFamilies.add(fontFamily);
        }
        System.out.println(fontFamilies.toString());
    }

    private void generatePreview(String fontFamily) {
        if (!fontAvailable(fontFamily)) {
            showFontUnavailableMessage();
            return;
        }
        BufferedImage previewImage = toImage(PREVIEW_STRING, getFont(fontFamily), Color.BLACK);
        if (!writeImageToDirectory(previewImage, fontFamily, "Preview")) {
            showIOErrorMessage();
        }
        showOperationFinishedMessage();
    }

    private void generateCards(String fontFamily) {
        if (!fontAvailable(fontFamily)) {
            showFontUnavailableMessage();
            return;
        }
        Font font = getFont(fontFamily);
        for (char character : PREVIEW_STRING.toCharArray()) {
            if (character == '1') {
                continue;
            }
            String stringChar;
            if (character == '0') {
                stringChar = "10";
            } else {
                stringChar = String.valueOf(character);
            }
            BufferedImage redCharacterImage = toImage(stringChar, font, new Color(213, 33, 40));
            BufferedImage blackCharacterImage = toImage(stringChar, font, Color.BLACK);
            if (!writeImageToDirectory(redCharacterImage, stringChar + "_red", "Characters")
                    || !writeImageToDirectory(blackCharacterImage,
                    stringChar + "_black", "Characters")) {
                showIOErrorMessage();
            }
        }
        showOperationFinishedMessage();
    }

    private BufferedImage toImage(String string, Font font, Color color) {
        BufferedImage image = new BufferedImage(font.getSize() * string.length(), font.getSize() * 2,
                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setFont(font);
        graphics.setColor(color);
        graphics.drawString(string, 0, image.getHeight() / 2);
        return removeExtra(image);
    }

    private BufferedImage removeExtra(BufferedImage image) {
        final int TRANSPARENT_WHITE_INT_RGB = 0;
        int leastX = image.getWidth() - 1;
        int greatestX = 0;
        int leastY = image.getHeight() - 1;
        int greatestY = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) != TRANSPARENT_WHITE_INT_RGB) {
                    if (x < leastX) {
                        leastX = x;
                    } else if (x > greatestX) {
                        greatestX = x;
                    }
                    if (y < leastY) {
                        leastY = y;
                    } else if (y > greatestY) {
                        greatestY = y;
                    }
                }
            }
        }
        return image.getSubimage(leastX, leastY, greatestX - leastX + 1, greatestY - leastY + 1);
    }

    private boolean writeImageToDirectory(BufferedImage image, String imageName, String directory) {
        try {
            createDirectoryIfNotExistent(directory);
            ImageIO.write(image, "png", new File(directory + "\\" + imageName + ".png"));
        } catch (IOException exception) {
            return false;
        }
        return true;
    }

    private void createDirectoryIfNotExistent(String directory) throws IOException {
        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            if (!directoryFile.mkdir()) {
                throw new IOException("Could not create directory");
            }
        }
    }

    private void showFontUnavailableMessage() {
        JOptionPane.showMessageDialog(this, "Font family chosen is unavailable",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showIOErrorMessage() {
        JOptionPane.showMessageDialog(this, "Error occurred while trying to create folder" +
                        " or writing image to folder",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showOperationFinishedMessage() {
        JOptionPane.showMessageDialog(this, "Operation finished", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean fontAvailable(String fontFamily) {
        return fontFamilies.contains(fontFamily);
    }

    private Font getFont(String fontFamily) {
        return new Font(fontFamily, Font.BOLD, 4000);
    }
}
