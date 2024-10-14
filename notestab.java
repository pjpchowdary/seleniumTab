WebElement notesTab = driver.findElement(By.xpath("//div[@id='tabs']//a[text()='Notes']"));
            notesTab.click();
/******/
 WebElement iframe = driver.findElement(By.cssSelector("iframe")); // Adjust selector as needed
            driver.switchTo().frame(iframe);

            // Access the shadow root
            WebElement shadowHost = driver.findElement(By.cssSelector("selector-for-shadow-host")); // Adjust selector as needed
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement shadowRoot = (WebElement) js.executeScript("return arguments[0].shadowRoot", shadowHost);

            // Find the tab inside the shadow root
            WebElement tab = shadowRoot.findElement(By.cssSelector("selector-for-tab")); // Adjust selector as needed
            tab.click(); // Click on the tab
