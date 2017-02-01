package name.neuhalfen.projects.crypto.bouncycastle.openpgp.reencryption;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.decrypting.DecryptWithOpenPGPInputStreamFactory;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.decrypting.DecryptionConfig;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.decrypting.SignatureValidationStrategies;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.encrypting.EncryptWithOpenPGP;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.encrypting.EncryptionConfig;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.testtooling.CatchCloseStream;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.testtooling.Configs;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assume.assumeNotNull;
import static org.mockito.Mockito.mock;

public class ReencryptExplodedZipSinglethreadTest {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ReencryptExplodedZipSinglethreadTest.class);

    private ZipEntityStrategy dummyStrategy = mock(ZipEntityStrategy.class);

    private ReencryptExplodedZipSinglethread sut() {
        return new ReencryptExplodedZipSinglethread();
    }

    @Test
    public void reencrypting_smallZip_doesNotCrash_integrationTest() throws Exception {

        try (
                final InputStream exampleEncryptedZip = CatchCloseStream.wrap("encrypted", getClass().getClassLoader().getResourceAsStream("testdata/zip_encrypted_binary_signed.zip.gpg"))
        ) {
            assumeNotNull(exampleEncryptedZip);

            final EncryptionConfig encryptionConfig = Configs.buildConfigForEncryptionFromResources();
            final DecryptionConfig decryptionConfig = Configs.buildConfigForDecryptionFromResources();

            assumeNotNull(encryptionConfig);
            assumeNotNull(decryptionConfig);

            EncryptWithOpenPGP encryptWithOpenPGP = new EncryptWithOpenPGP(encryptionConfig);

            DecryptWithOpenPGPInputStreamFactory decription = new DecryptWithOpenPGPInputStreamFactory(decryptionConfig, SignatureValidationStrategies.requireAnySignature());

            try (
                    final InputStream plainTextStream = CatchCloseStream.wrap("plain", decription.wrapWithDecryptAndVerify(exampleEncryptedZip))
            ) {

                sut().explodeAndReencrypt(plainTextStream, this.dummyStrategy, encryptWithOpenPGP);
            }
        }
    }

}
