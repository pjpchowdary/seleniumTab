private static void takeFullPageScreenshot(WebDriver driver, String fileName) throws IOException {
    // Get the total height of the page
    Long totalHeight = (Long) ((JavascriptExecutor) driver).executeScript("return document.body.parentNode.scrollHeight");
    Long windowHeight = (Long) ((JavascriptExecutor) driver).executeScript("return window.innerHeight");
    int scrollHeight = windowHeight.intValue();
    int totalScrolls = (int) Math.ceil(((double) totalHeight) / scrollHeight);

    BufferedImage fullImg = null;
    Graphics2D g2d = null;

    for (int i = 0; i < totalScrolls; i++) {
        // Scroll to the appropriate part of the page
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + (i * scrollHeight) + ")");
        
        // Wait for the page to load after scrolling
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Take screenshot of the current viewport
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(screenshot);

        // Initialize the full image if it's the first iteration
        if (fullImg == null) {
            fullImg = new BufferedImage(img.getWidth(), totalHeight.intValue(), BufferedImage.TYPE_INT_RGB);
            g2d = fullImg.createGraphics();
        }

        // Draw the screenshot at the appropriate position in the full image
        g2d.drawImage(img, 0, i * scrollHeight, null);
    }

    // Dispose of graphics context
    if (g2d != null) {
        g2d.dispose();
    }

    // Save the full screenshot
    if (fullImg != null) {
        File destFile = new File(SCREENSHOT_FOLDER + File.separator + fileName + ".png");
        ImageIO.write(fullImg, "png", destFile);
        System.out.println("Full page screenshot saved: " + destFile.getAbsolutePath());
    }
}
