import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

private static void takeFullPageScreenshot(WebDriver driver, String fileName) throws IOException {
    // Scroll to the top of the page
    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");

    List<BufferedImage> images = new ArrayList<>();
    long lastHeight = -1;
    int maxAttempts = 50; // Prevent infinite loop
    int attempts = 0;

    while (attempts < maxAttempts) {
        // Take screenshot of current viewport
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage image = ImageIO.read(screenshot);
        images.add(image);

        // Get current scroll height
        long currentHeight = (Long) ((JavascriptExecutor) driver).executeScript("return document.documentElement.scrollTop || document.body.scrollTop;");

        // Scroll down by viewport height
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, window.innerHeight);");

        // Wait for page to load after scrolling
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if we've reached the bottom
        long newHeight = (Long) ((JavascriptExecutor) driver).executeScript("return document.documentElement.scrollTop || document.body.scrollTop;");
        if (newHeight == lastHeight) {
            // We've reached the bottom if the height hasn't changed
            break;
        }

        lastHeight = newHeight;
        attempts++;
    }

    // Combine images
    int totalWidth = images.get(0).getWidth();
    int totalHeight = images.stream().mapToInt(BufferedImage::getHeight).sum();

    BufferedImage combined = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
    Graphics g = combined.getGraphics();

    int heightCursor = 0;
    for (BufferedImage image : images) {
        g.drawImage(image, 0, heightCursor, null);
        heightCursor += image.getHeight();
    }
    g.dispose();

    // Save the combined image
    File destFile = new File(SCREENSHOT_FOLDER + File.separator + fileName + ".png");
    ImageIO.write(combined, "png", destFile);
    System.out.println("Full page screenshot saved: " + destFile.getAbsolutePath());
}
