package bmacnamara.neueda.assessment;

import bmacnamara.neueda.assessment.model.Account;

public class TestUtils {

    public static final int MOCK_ACCT_NUMBER = 123456789;
    public static final int MOCK_ACCT_PIN = 1234;
    public static final double MOCK_ACCT_BALANCE = 800;
    public static final double MOCK_ACCT_OVERDRAFT = 200;

    public static Account getMockAccount() {
        Account mockAccount = new Account();
        mockAccount.setAccountNumber(MOCK_ACCT_NUMBER);
        mockAccount.setPin(MOCK_ACCT_PIN);
        mockAccount.setBalance(MOCK_ACCT_BALANCE);
        mockAccount.setOverdraft(MOCK_ACCT_OVERDRAFT);
        return mockAccount;
    }

}
