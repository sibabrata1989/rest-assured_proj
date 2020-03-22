package library_package;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.List;

public class TestStatistics implements ITestListener {
    public static List<ITestNGMethod> passedtests = new ArrayList<ITestNGMethod>();
    public static List<ITestNGMethod> failedtests = new ArrayList<ITestNGMethod>();
    public static List<ITestNGMethod> skippedtests = new ArrayList<ITestNGMethod>();


    @Override

    //This method will automatically be called if a test runs successfully
    public void onTestSuccess(ITestResult result) {

        //add the passed tests to the passed list

        passedtests.add(result.getMethod());

    }

    @Override

    //This method will automatically be called if a test fails
    public void onTestFailure(ITestResult result) {

        //add the failed tests to the failed list
        failedtests.add(result.getMethod());

    }

    @Override

    //This method will automatically be called if a test is skipped
    public void onTestSkipped(ITestResult result) {

        //add the skipped tests to the skipped list
        skippedtests.add(result.getMethod());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        System.out.println("Test Failed but within success percentage" +iTestResult.getName());
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("This is onStart method" +iTestContext.getOutputDirectory());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("This is onFinish method" +iTestContext.getPassedTests());
        System.out.println("This is onFinish method" +iTestContext.getFailedTests());
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        System.out.println("New Test Started" +iTestResult.getName());
    }
}
