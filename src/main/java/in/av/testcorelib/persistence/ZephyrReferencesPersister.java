package in.av.testcorelib.persistence;

import in.av.testcorelib.persistence.mapping.TestCase;
import in.av.testcorelib.persistence.mapping.TestSuite;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;


import java.io.File;
import java.util.List;

public class ZephyrReferencesPersister implements ReferencesPersister{

    private final String outputFolder;

    public ZephyrReferencesPersister(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public void persist(List<TestCase> testCases) {
        try {
            File dir = new File(outputFolder);
            if (!dir.exists())
                dir.mkdir();
            File report = new File(outputFolder + "ZEPHYR-REPORT-" + testCases.get(0).getClassName() + ".xml");
            if (!report.exists())
                report.createNewFile();
            JAXBContext context = JAXBContext.newInstance(TestSuite.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            TestSuite testSuite = new TestSuite();
            testSuite.setTestCases(testCases);
            marshaller.marshal(testSuite, report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist Zephyr Report", e);
        }
    }
}
