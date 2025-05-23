package ZBRA.tfm;

import ZBRA.blockchain.Block;
import ZBRA.blockchain.Data;
import ZBRA.blockchain.Miner;
import ZBRA.blockchain.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;

public class FirstPrice extends AbstractTFM {
    private final static String type = "1st Price Auction";

    public FirstPrice() {
        super(type);
    }

    // used for logging each tx data
    public String[] logStart(int i, String h, double f) {
        return new String[] {
                String.valueOf(i),
                h,
                String.valueOf(f),
        };
    }

    @Override
    public String[] logHeaders() {
       return new String[] {
                "Time",
                "Block Height",
                "Parent Hash",
                "Current Hash",
                "Miner ID",
                "Block Reward",
                "Size Limit",
                "Block Size",
                "Number of TX",
                "TX Index",
                "TX Hash",
                "TX Fee"
        };
    }

    // Main First-Price Mechanism Implementation
    @Override
    public Data fetchValidTX(ArrayList<Transaction> m, double blockLimit, Block b, Miner miner, double target) {
        // sort current mempool by highest fee per byte price offered
        m.sort((t1, t2) -> Double.compare(t2.getByte_fee(), t1.getByte_fee()));

        ArrayList<String[]> logs = new ArrayList<String[]>(); // log data for printing later
        ArrayList<Transaction> confirmed = new ArrayList<Transaction>(); // list of *confirmed* transactions
        double sizeUsedUp = 0; // total bytes used by current block
        BigDecimal rewards = new BigDecimal("0"); // total rewards given to miner

        int index = 1;

        while(true) {
            // if mempool is not empty..
            if (!m.isEmpty()) {
                // if block is not yet filled to capacity..
                if ((sizeUsedUp + m.get(0).getSize()) < blockLimit) {
                    // add to *confirmed* tx list
                    confirmed.add(m.get(0));

                    // update parameters
                    sizeUsedUp += m.get(0).getSize();
                    rewards = rewards.add((new BigDecimal(m.get(0).getTotal_fee())));

                    // log data
                    logs.add(logStart(index, m.get(0).getHash(), m.get(0).getTotal_fee()));

                    // remove from mempool and continue..
                    m.remove(0);
                    index++;
                } else {
                    break;
                }
            }
            else {
                break;
            }
        }

        return new Data(m, confirmed, rewards, sizeUsedUp, logs);
    }
}
