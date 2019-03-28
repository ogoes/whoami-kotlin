import XCTest

#if !os(macOS)
public func allTests() -> [XCTestCaseEntry] {
    return [
        testCase(whoami_swiftTests.allTests),
    ]
}
#endif