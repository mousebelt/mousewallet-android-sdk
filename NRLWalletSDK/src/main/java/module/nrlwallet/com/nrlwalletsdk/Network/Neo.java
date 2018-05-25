package module.nrlwallet.com.nrlwalletsdk.Network;


import io.github.novacrypto.bip32.Network;

public enum Neo implements Network {
    MAIN_NET {
        @Override
        public int getPrivateVersion() {
            return 0x0488B21E;
        }

        @Override
        public int getPublicVersion() {
            return 0x0488ADE4;
        }

        @Override
        public byte p2pkhVersion() {
            return 0x35;
        }

        @Override
        public byte p2shVersion() {
            return 0x05;
        }
    }
}