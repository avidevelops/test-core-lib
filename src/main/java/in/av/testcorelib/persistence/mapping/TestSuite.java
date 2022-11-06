package in.av.testcorelib.persistence.mapping;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "testsuite")
public class TestSuite {
    private List<TestCase> testCases;

    public List<TestCase> getTestCases() {
        return testCases;
    }

    @XmlElement(name = "testcase")
    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}
