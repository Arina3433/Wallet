package org.example.wallet;

import org.example.wallet.api.mappers.WalletMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {WalletMapperImpl.class})
class WalletApplicationTests {

    @Test
    void contextLoads() {
    }

}
