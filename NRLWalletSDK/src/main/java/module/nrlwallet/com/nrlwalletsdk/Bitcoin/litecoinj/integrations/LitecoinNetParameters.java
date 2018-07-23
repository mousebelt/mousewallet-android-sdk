package module.nrlwallet.com.nrlwalletsdk.Bitcoin.litecoinj.integrations;

import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.*;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.params.AbstractBitcoinNetParams;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.script.Script;
import module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.script.ScriptOpCodes;

import java.io.ByteArrayOutputStream;

import static com.google.common.base.Preconditions.checkState;
import static module.nrlwallet.com.nrlwalletsdk.Bitcoin.bitcoinj.core.Coin.FIFTY_COINS;

/**
 * Created by over on 08.12.14.
 */
public class LitecoinNetParameters extends AbstractBitcoinNetParams{

    public LitecoinNetParameters() {
        super();
//        alertSigningKey = Utils.HEX.decode("040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9");
//        blockVerifyFunction = new LitecoinVerifyFunction();
        targetTimespan = TARGET_TIMESPAN;
        interval = INTERVAL;
        maxTarget = Utils.decodeCompactBits(0x1e0fffffL);
        dumpedPrivateKeyHeader = 176;
        addressHeader = 48;
        p2shHeader = 5;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        port = 9333;
        packetMagic = 0xFBC0B6DBL;
        bip32HeaderPub = 0x0488B21E; //The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x0488ADE4; //The 4 byte header that serializes in base58 to "xprv"

        majorityEnforceBlockUpgrade = 1000;
        majorityRejectBlockOutdated = 950;
        majorityWindow = 750;

/*
//        genesisBlock = createEmpty(this);
        Transaction t = new Transaction(this);
        try {
            // A script containing the difficulty bits and the following message:
            //
            //   "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks"
            byte[] bytes = Utils.HEX.decode
                    ("04ffff001d0104404e592054696d65732030352f4f63742f32303131205374657665204a6f62732c204170706c65e280997320566973696f6e6172792c2044696573206174203536");
            t.addInput(new TransactionInput(this, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Utils.HEX.decode
                    ("040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(this, t, FIFTY_COINS, scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }
        genesisBlock.addTransaction(t);
*/
        genesisBlock.setDifficultyTarget(504365040);
        genesisBlock.setTime(1529131310L);//1317972665L
        genesisBlock.setNonce(2084524493);
        id = "org.litecoin.production";
        subsidyDecreaseBlockCount = 840000;
        spendableCoinbaseDepth = 100;
        String genesisHash = genesisBlock.getHashAsString();
        System.out.println("Genesis hash:" + genesisHash);
//        checkState(genesisHash.equals("12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2"),
//                genesisHash);

        // This contains (at a minimum) the blocks which are not BIP30 compliant. BIP30 changed how duplicate
        // transactions are handled. Duplicated transactions could occur in the case where a coinbase had the same
        // extraNonce and the same outputs but appeared at different heights, and greatly complicated re-org handling.
        // Having these here simplifies block connection logic considerably.
   /*     checkpoints.put( 0, Sha256Hash.wrap("12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2"));
        checkpoints.put( 20160, Sha256Hash.wrap("633036c8df655531c2449b2d09b264cc0b49d945a89be23fd3c1a97361ca198c"));
        checkpoints.put( 40320, Sha256Hash.wrap("d148cdd2cf44069cef4b63f0feaf30a8d291ca9ea9ba7e83f226b9738c1d5e9c"));
        checkpoints.put( 60480, Sha256Hash.wrap("3250f0a560d55f039c34bfaee1b71297aa5104ac6641778f9a87d73232d12c6c"));
        checkpoints.put( 80640, Sha256Hash.wrap("bedc0a090b740b1902d870aeb6caa89040a24e7d670d46f8ef035fd9d2e9ce80"));
        checkpoints.put( 100800, Sha256Hash.wrap("7b0b620d15f781faaaa73b43607a49d5becb2b803ef19b4010014646cc177a61"));
        checkpoints.put( 120960, Sha256Hash.wrap("dbd6249f30e5690890bc03dabcc0a526c46adcde572be06af4075b6ea28aa251"));
        checkpoints.put( 141120, Sha256Hash.wrap("5d5e15a45cecf2b9528e36e63c407167423a2f9963a96bbce3b67b75fd10be2a"));
        checkpoints.put( 161280, Sha256Hash.wrap("f595c754d0abcfe3616573bfabee01b230ec0ea6b2f2894c40214ea23d772b6c"));
        checkpoints.put( 181440, Sha256Hash.wrap("d7fa3152959f3c25e33edf825f7cbef75ee651d5f9183cc4ed8d19d57b8f35a4"));
        checkpoints.put( 201600, Sha256Hash.wrap("d481df8e8ce144fca9ae6b3157cc706e903c6ea161a13d2c421270354a02d6d0"));
        checkpoints.put( 221760, Sha256Hash.wrap("88cf3446129161a633050244f112e3041a2d53152ee9293984b20f468fbadb8a"));
        checkpoints.put( 241920, Sha256Hash.wrap("8619aa9c734b517bd3a707278ee3632c96570f3e1fd804194bdfc0b02d1b6c4e"));
        checkpoints.put( 262080, Sha256Hash.wrap("13a5d47f01fe3ab17ebf2b15b605efa41efe06b02bb685bc2ad4cec22af0b478"));
        checkpoints.put( 282240, Sha256Hash.wrap("8932095fba44bd6860fd71745c0dca908769221a47166ab1fb442b6cefcd53fb"));
        checkpoints.put( 302400, Sha256Hash.wrap("e798d897a837bf4989d329266128754ec1cbeff1eb0c0afd67f71d2b7c44bdaa"));
        checkpoints.put( 322560, Sha256Hash.wrap("3e5857760633de4604d388fed7126a22ba840ea320c8cde6a84df981bc8b751d"));
        checkpoints.put( 342720, Sha256Hash.wrap("33f62e026a202be550e8a9df37d638d38991553544e279cb264123378bf46042"));
        checkpoints.put( 362880, Sha256Hash.wrap("77a4b194e8c7f6600ed622b8f60cb9d96eeb0a0b837201e605de14016edfda39"));
        checkpoints.put( 383040, Sha256Hash.wrap("5c0a443361c1356796a7db472c69433b6ce6108d61e4403fd9a9d91e01009ce3"));
        checkpoints.put( 403200, Sha256Hash.wrap("ef78aa1925cc51ff8dc3a1e59f389c89845fb8b9e566348222e663e963e67640"));
        checkpoints.put( 423360, Sha256Hash.wrap("7b23f9447b8078c8fc0e832e4b56f1d2afa758382e254593b6b72a8fc6020150"));
        checkpoints.put( 443520, Sha256Hash.wrap("37d668803ed1efc24ffab4a2a90da9ac92679acf68370d7570f042c2bd6d651b"));
        checkpoints.put( 463680, Sha256Hash.wrap("260c78e92a390b9eb4d8f5d9324a33d0222943f119b324de53452d48bd7bd7f4"));
        checkpoints.put( 483840, Sha256Hash.wrap("759de6c4e6161fc8c996cf0d5e012ee0afc52a037e657dd54e85da9a9f803633"));
        checkpoints.put( 504000, Sha256Hash.wrap("97db0624d3d5137bc085f0d731607314972bb4124b85b73420ef9aa5fc10d640"));
        checkpoints.put( 524160, Sha256Hash.wrap("1d033d3abedb7faa15dad1bbe9c7fc7151746537cf091584be567d321e7c5cd0"));
        checkpoints.put( 544320, Sha256Hash.wrap("95ae252971d1ec9deeed1ed19fe9537e04348a82839a9e2bf8856faaa03e324e"));
        checkpoints.put( 564480, Sha256Hash.wrap("c876276bf12754c2b265787d9e7ab83d429e59761dc63057f728529018db7834"));
        checkpoints.put( 584640, Sha256Hash.wrap("df5454af79491c392fe740b5efd47afbe1cb53cd8d86be3ab9c97fdd2786d237"));
        checkpoints.put( 604800, Sha256Hash.wrap("43c1a80b8abaf57817e5daea9cfdde99ea5f324705779045792ccad52d54f3d4"));
        checkpoints.put( 624960, Sha256Hash.wrap("ccac71fafe98107b81ac3e0eed41190e4d47600962c93c49db8843b53f760bda"));
        checkpoints.put( 645120, Sha256Hash.wrap("9b7ddc3753c5138fc471accd15f9730020e828bc69058f2e382549c7c0ffba0f"));
        checkpoints.put( 665280, Sha256Hash.wrap("163c902de2306f22922754f83edacc97a87617d1e3413af7c9808e702bf1a383"));
        checkpoints.put( 685440, Sha256Hash.wrap("29d2328990dda4c4870846d4e3d573785452bed68e6013930a83fc8d5fe89b09"));
        checkpoints.put( 705600, Sha256Hash.wrap("e350118d9047c1ca5f047a1b1ee400562fb0cfb8b3c8032b56b8545b456a03ab"));
        checkpoints.put( 725760, Sha256Hash.wrap("6b2ac7ffb71fc5056c00fee8404813d7ea98e5f303a5ddb26c09fb397b51b7e7"));
        checkpoints.put( 745920, Sha256Hash.wrap("04809a35ff6e5054e21d14582072605b812b7d4ae11d3450e7c03a7237e1d35d"));
        checkpoints.put( 766080, Sha256Hash.wrap("ba9e143a958c917753785f11c143ca62f928748c33888278fcaea96f054f15d2"));
        checkpoints.put( 786240, Sha256Hash.wrap("d1b9fa6999f7a09d1dc52511750e47d263aaa7ea4a262762fff8665890d631a5"));
        checkpoints.put( 806400, Sha256Hash.wrap("e2363e8b3e8f237b9b1bfc1c72ede80fef2c7bd1aabcd78afed82065a194b960"));
        checkpoints.put( 826560, Sha256Hash.wrap("e12ce49268950a38fd7f0bab0d2a5edd9799201c1f3e9441a7602428556c839d"));
        checkpoints.put( 846720, Sha256Hash.wrap("6f5d94d7cfd01f1dbf4aa631b987f8e2ec9d0c57720604787b816bafe34192a8"));
        checkpoints.put( 866880, Sha256Hash.wrap("72a9f3d3710fc6c96f87dd8fca0e033a1a89f69a4c2fd8944fd1d50e6772021e"));
        checkpoints.put( 887040, Sha256Hash.wrap("089c03de0c0dd0dffaa044fd5a3b51679be2ae34b048a8d6bcc39aab664c156a"));
        checkpoints.put( 889056, Sha256Hash.wrap("910af99e39a6f9436bf4710a09ee19483e9b9b3f131dc9bef37dbe5eac72031f"));
        checkpoints.put( 901152, Sha256Hash.wrap("cfccdf8e3830ae4879e910051ac3dc583b4fb45b83be3a38019e5d9326dfa223"));
        checkpoints.put( 913248, Sha256Hash.wrap("9784249cbeccd4df8d7701287da3002a6de4a56618248f84f37187dbf4ec6efc"));
        checkpoints.put( 921312, Sha256Hash.wrap("ab2357460c0a20caebfab76a7939c4e64a5068eddce4fbec749089be2e88e702"));
        checkpoints.put( 933408, Sha256Hash.wrap("f9f3fbcbb1fa40d0f9a1724085ac7cadaa414edd97c436571d06b3b5f3b46956"));
        checkpoints.put( 941472, Sha256Hash.wrap("4fddb941d414f071c29f100da2a160cf527397fc9a7a9c9d0a849b6f67799042"));
        checkpoints.put( 953568, Sha256Hash.wrap("e46e01cf1239cffa69408ac162d517bac5a4899972e0328fd0ba4d93e8ad3764"));
        checkpoints.put( 961632, Sha256Hash.wrap("bfc01091cb21ea81dd079fcee6cf7910087281bfdbcb1ad9e5dbc226b5f45a86"));
        checkpoints.put( 973728, Sha256Hash.wrap("6316b454ead6c97be48c98979ec9ebb49763c21d436f47ff6918f02a58b46cec"));
        checkpoints.put( 981792, Sha256Hash.wrap("155bc8fb717564bd2dd600cedcb39d8a7a64070e3bc1b90e7be62168e7b35c82"));
        checkpoints.put( 993888, Sha256Hash.wrap("1d80e7793bd9e16e0ce84d93b105d6732ed63e1a6fe491c1b7ea310e75eb504e"));
        checkpoints.put( 1001952, Sha256Hash.wrap("eccbede26ac99ea996377972d5bd05b9306bcc6ac1f4071f1587e3094a704dff"));
        checkpoints.put( 1058400, Sha256Hash.wrap("76ce37c66d449a4ffbfc35674cf932da701066a001dc223754f9250dd2bdbc62"));
        checkpoints.put( 1260000, Sha256Hash.wrap("85a22b528d805bf7a641d1d7c6d96ef5054beda3dcab6be7b83f2e3df24b33a8"));
*/

        checkpoints.put(1500, Sha256Hash.wrap("12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2"));
        checkpoints.put(4032, Sha256Hash.wrap("633036c8df655531c2449b2d09b264cc0b49d945a89be23fd3c1a97361ca198c"));
        checkpoints.put(8064, Sha256Hash.wrap("eb984353fc5190f210651f150c40b8a4bab9eeeff0b729fcb3987da694430d70"));
        checkpoints.put(16128, Sha256Hash.wrap("602edf1859b7f9a6af809f1d9b0e6cb66fdc1d4d9dcd7a4bec03e12a1ccd153d"));
        checkpoints.put(23420, Sha256Hash.wrap("d80fdf9ca81afd0bd2b2a90ac3a9fe547da58f2530ec874e978fce0b5101b507"));
        checkpoints.put(50000, Sha256Hash.wrap("69dc37eb029b68f075a5012dcc0419c127672adb4f3a32882b2b3e71d07a20a6"));
        checkpoints.put(80000, Sha256Hash.wrap("4fcb7c02f676a300503f49c764a89955a8f920b46a8cbecb4867182ecdb2e90a"));
        checkpoints.put(120000, Sha256Hash.wrap("bd9d26924f05f6daa7f0155f32828ec89e8e29cee9e7121b026a7a3552ac6131"));
        checkpoints.put(161500, Sha256Hash.wrap("dbe89880474f4bb4f75c227c77ba1cdc024991123b28b8418dbbf7798471ff43"));
        checkpoints.put(179620, Sha256Hash.wrap("2ad9c65c990ac00426d18e446e0fd7be2ffa69e9a7dcb28358a50b2b78b9f709"));
        checkpoints.put(240000, Sha256Hash.wrap("7140d1c4b4c2157ca217ee7636f24c9c73db39c4590c4e6eab2e3ea1555088aa"));
        checkpoints.put(383640, Sha256Hash.wrap("2b6809f094a9215bafc65eb3f110a35127a34be94b7d0590a096c3f126c6f364"));
        checkpoints.put(409004, Sha256Hash.wrap("487518d663d9f1fa08611d9395ad74d982b667fbdc0e77e9cf39b4f1355908a3"));
        checkpoints.put(456000, Sha256Hash.wrap("bf34f71cc6366cd487930d06be22f897e34ca6a40501ac7d401be32456372004"));
        checkpoints.put(541794, Sha256Hash.wrap("1cbccbe6920e7c258bbce1f26211084efb19764aa3224bec3f4320d77d6a2fd2"));
        checkpoints.put(585010, Sha256Hash.wrap("ea9ea06840de20a18a66acb07c9102ee6374ad2cbafc71794e576354fea5df2d"));
        checkpoints.put(638902, Sha256Hash.wrap("15238656e8ec63d28de29a8c75fcf3a5819afc953dcd9cc45cecc53baec74f38"));

        dnsSeeds = new String[] {
                "dnsseed.litecointools.com",
                "dnsseed.litecoinpool.org",
                "dnsseed.koin-project.com",
                "dnsseed.weminemnc.com"
        };
//        dnsSeeds = new String[]{
//                "debby"
//        };
        addrSeeds = new int[]{
                0x1ddb1032, 0x6242ce40, 0x52d6a445, 0x2dd7a445, 0x8a53cd47, 0x73263750, 0xda23c257, 0xecd4ed57,
        };
    }

    private static LitecoinNetParameters instance;
    public static synchronized LitecoinNetParameters get() {
        if (instance == null) {
            instance = new LitecoinNetParameters();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return "litecoin main";
    }
}
