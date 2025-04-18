package testing;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Main {
	
	public static WebDriver driver;	
	public static WebDriverWait wait;
	public static Actions act;
	private String fromCity;
	private String toCity;
	private String travelClass;
	public Main(String fromCity,String toCity,String travelClass) {
		this.fromCity=fromCity;
		this.toCity=toCity;
		this.travelClass=travelClass;
	}
	public WebDriver createDriver() {
		WebDriverManager.edgedriver().setup();
		driver=new EdgeDriver();
		wait=new WebDriverWait(driver, Duration.ofSeconds(100));
		act=new Actions(driver);
		//WebDriverManager.chromedriver().setup();
		//WebDriver driver=new ChromeDriver();				
		return driver;
	}
	
	public void openUrl(String url) {
		driver.get(url);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}
	
	public void initateFlightSearch() throws InterruptedException {
		WebElement from = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("stationFrom")));			
		from.sendKeys(fromCity);
		Thread.sleep(2000);
		WebElement hydOption = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), '"+fromCity+"')]")
                ));
           act.moveToElement(hydOption).click(hydOption).perform();
	    

        WebElement to = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("stationTo"))); 
        to.sendKeys(toCity);
        Thread.sleep(2000);
        WebElement puneOption = wait.until(ExpectedConditions.elementToBeClickable(
        		    By.xpath("//div[contains(text(), '"+toCity+"')]")
        		));
           
           act.moveToElement(puneOption).click(puneOption).perform();
           	          
		
          WebElement date=driver.findElement(By.id("originDate"));
          date.click();
          Thread.sleep(2000);
          LocalDate d=LocalDate.now();
          int cu=d.getDayOfMonth();
          String currentDate=cu+"";
          String monthName = d.format(DateTimeFormatter.ofPattern("MMMM"));
          DayOfWeek dayOfWeek = d.getDayOfWeek();
          String dayAsString = dayOfWeek.toString(); 
          String day=dayAsString.substring(0,3)+", "+currentDate+" "+monthName.substring(0,3);
          
          Thread.sleep(3000);
  		List<WebElement> allDates = driver.findElements(By.xpath("//table[@class='date-table']//td//span"));
  		Thread.sleep(2000);
  		for(WebElement e:allDates) {
  			if(e.getText().contains(currentDate)) {
  				Thread.sleep(1000);
  				
  				e.click();
  				break;
  			}
  		}
          driver.findElement(By.id("noOfpaxEtc")).click();
		  WebElement travelType=driver.findElement(By.id("travelClass"));
		  Select dropdown=new Select(travelType);
		  dropdown.selectByVisibleText(travelClass);
		  
		  driver.findElement(By.xpath("//button[contains(text(),'Search')]")).click();
		  Thread.sleep(5000);
	}
	
	public void verifySearchResults() {
		LocalDate d=LocalDate.now();
        int cu=d.getDayOfMonth();
        String currentDate=cu+"";
        String monthName=d.format(DateTimeFormatter.ofPattern("MMMM"));
        DayOfWeek dayOfWeek=d.getDayOfWeek();
        String dayAsString=dayOfWeek.toString(); 
        String day=dayAsString.substring(0,3)+", "+currentDate+" "+monthName.substring(0,3);
        
        WebElement stationFromElement=wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("stationFrom")));
        String fromCity=stationFromElement.getAttribute("value");	
        WebElement stationToElement=wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("stationTo")));
        String toCity=stationToElement.getAttribute("value");	
			
		List<WebElement> fromName=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='right-searchbarbtm-in']/div[2]//label/following-sibling::span")));
		  			  
		  for (WebElement e : fromName) {
		      if (!e.getText().equals(fromCity)) {
		    	  driver.close();
		          System.out.println("From city is not matching: Expected " + fromCity + ", but found " + e.getText());
		          
		      }
		  }
		 
		  List<WebElement> toName=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='right-searchbarbtm-in']/div[3]//label/following-sibling::span")));
		  		  
		  for (WebElement e : toName) {
		      if (!e.getText().equals(toCity)) {
		          driver.close();
		          System.out.println("To city is not matching: Expected " + toCity + ", but found " + e.getText());
		          break;
		      }
		  }
		  WebElement co=driver.findElement(By.xpath("//main/div/div/div[2]/div[1]/div/div/div[1]/a/span[1]"));			 
		  if(!day.equalsIgnoreCase(co.getText())) {
			  driver.close();
			  System.out.print("Date not matching");
		  }
		  
	}
	
	public void displayResults() {
		List<WebElement> list=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='right-searchbarbtm-in']")));
		  System.out.println("Total Number of Flights available are : "+list.size());			 
		  List<WebElement> name1=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='right-searchbarbtm-in']/descendant::b")));
		  List<WebElement> name2=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='right-searchbarbtm-in']//b/following-sibling::span")));
		  System.out.println("Flight Names");
		  for (int i = 0; i < list.size(); i++) { 
		      System.out.println(name1.get(i).getText() + " " + name2.get(i).getText());
		  }
	}
	public void captureScreenshot() throws InterruptedException {
		WebElement pos=driver.findElement(By.xpath("//main/div/div/div[2]"));			 
		  Thread.sleep(1000);
		  JavascriptExecutor js=(JavascriptExecutor)driver;
		  js.executeScript("document.body.style.zoom='40%'");
		  Thread.sleep(2000);
		  File f=pos.getScreenshotAs(OutputType.FILE);
		  File s = new File(System.getProperty("user.dir")+"\\Result Screenshots\\file.png");
		  f.renameTo(s);
		  Thread.sleep(2000);
		  driver.close();
	}
	public static void main(String[] args) throws InterruptedException {
		
		Main m=new Main("Hyd","Pun","Business");
		//Main m=new Main("Delhi","Mum","Business");
		WebDriver driver=m.createDriver();
		String url="https://www.air.irctc.co.in/";
		m.openUrl(url);
		m.initateFlightSearch();	
		m.verifySearchResults();
		m.displayResults();
		m.captureScreenshot();

	}

}

