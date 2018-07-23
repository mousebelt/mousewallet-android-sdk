/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package module.nrlwallet.com.nrlwalletsdk.libdohj.params;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Sha256Hash;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Utils;
import org.spongycastle.util.encoders.Hex;

import static com.google.common.base.Preconditions.checkState;
import java.io.ByteArrayOutputStream;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.AltcoinBlock;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Block;
import static module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Coin.COIN;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.NetworkParameters;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Transaction;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionInput;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.TransactionOutput;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.params.AbstractBitcoinNetParams;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.params.MainNetParams;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.script.Script;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.script.ScriptOpCodes;

/**
 * Parameters for the Litecoin main production network on which people trade
 * goods and services.
 */

public class LitecoinMainNetParams extends AbstractBitcoinNetParams {
    public static final int MAINNET_MAJORITY_WINDOW = MainNetParams.MAINNET_MAJORITY_WINDOW;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = MainNetParams.MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = MainNetParams.MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;

    public LitecoinMainNetParams() {
        super();
        id = ID_LITECOIN_MAINNET;
        // Genesis hash is 12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2
        packetMagic = 0xfbc0b6db;

        maxTarget = Utils.decodeCompactBits(0x1e0fffffL);
        port = 9333;
        addressHeader = 48;
        p2shHeader = 5;
        acceptableAddressCodes = new int[]{addressHeader, p2shHeader};
        dumpedPrivateKeyHeader = 176;

        spendableCoinbaseDepth = 100;
        subsidyDecreaseBlockCount = 840000;

        genesisBlock.setTime(1317972665L);
        genesisBlock.setDifficultyTarget(0x1e0ffff0L);
        genesisBlock.setNonce(2084524493);

        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("5155a7ed2219a75c0735c58b5d459c6d07d97917570e27b9d1d4546fb8431381"));
        alertSigningKey = Hex.decode("040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9");

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        dnsSeeds = new String[]{
                "dnsseed.litecointools.com",
                "dnsseed.litecoinpool.org",
                "dnsseed.ltc.xurious.com",
                "dnsseed.koin-project.com",
                "dnsseed.weminemnc.com"
        };
        bip32HeaderPub = 0x0488B21E;
        bip32HeaderPriv = 0x0488ADE4;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }


    private static LitecoinMainNetParams instance;

    public static synchronized LitecoinMainNetParams get() {
        if (instance == null) {
            instance = new LitecoinMainNetParams();
        }
        return instance;
    }
}