package in.av.testcorelib.persistence;

import in.av.testcorelib.persistence.mapping.TestCase;

import java.util.List;

/**
 * persists the id (from annotations) picked up by the run listener
 */
public interface ReferencesPersister {
    void persist(List<TestCase> testCases);
}
