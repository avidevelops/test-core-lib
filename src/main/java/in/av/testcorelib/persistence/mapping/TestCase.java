package in.av.testcorelib.persistence.mapping;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

public class TestCase {
    private String name;
    private String time;
    private boolean passed;
    private Skipped skipped;
    private Failure failure;
    private String className;
    private List<String> issueKeys;
    private boolean createIssueInZephyr = false;

    public TestCase() {
        this.passed = true;
    }

    public boolean isPassed() {
        return passed;
    }

    public boolean isFailed() {
        return failure != null;
    }

    public boolean isSkipped() {
        return skipped != null;
    }

    public Skipped getSkipped() {
        return skipped;
    }

    public boolean getCreateIssueInZephyr() {
        return createIssueInZephyr;
    }

    @XmlAttribute(name = "createissue")
    public void setCreateIssueInZephyr(boolean createIssueInZephyr) {
        this.createIssueInZephyr = createIssueInZephyr;
    }

    public List<String> getIssueKeys() {
        return issueKeys;
    }

    @XmlElement(name = "issuekey")
    public void setIssueKeys(List<String> issueKeys) {
        this.issueKeys = issueKeys;
    }

    @XmlElement(name = "skipped")
    public void setSkipped(Skipped skipped) {
        this.skipped = skipped;
        this.passed = false;
    }

    public Failure getFailure() {
        return failure;
    }

    @XmlElement(name = "failure")
    public void setFailure(Failure failure) {
        this.failure = failure;
        this.passed = false;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    @XmlAttribute(name = "classname")
    public void setClassName(String className) {
        this.className = className;
    }

    public String getTime() {
        return time;
    }

    @XmlAttribute(name = "time")
    public void setTime(String time) {
        this.time = time;
    }
}
