package info.skydark.maidpro;

import littleMaidMobX.LMM_EntityLittleMaid;

import java.lang.reflect.Field;

/**
 * Created by skydark on 15-8-29.
 */
public class MaidContract {
    private Field maidContract;
    private LMM_EntityLittleMaid maid;
    public MaidContract(LMM_EntityLittleMaid maid) {
        this.maid = maid;
        maidContract = null;
        Class<? extends LMM_EntityLittleMaid> cls = maid.getClass();
        try {
            maidContract = cls.getDeclaredField("maidContractLimit");
            maidContract.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean setContract(int maidContractLimit) {
        if (maidContract == null) {
            return false;
        }
        try {
            maidContract.set(maid, new Integer(maidContractLimit));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getContract() {
        if (maidContract == null) {
            return -1;
        }
        int maidContractLimit = -1;
        try {
            maidContractLimit = ((Integer) maidContract.get(maid)).intValue();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return maidContractLimit;
    }

    public boolean addContract(int maidContractLimit) {
        int maidContract = getContract();
        if (maidContract < 0) {
            return false;
        }
        maidContractLimit += maidContract;
        if (maidContractLimit > 168000) {
            maidContractLimit = 168000;    // 24000 * 7
        }
        setContract(maidContractLimit);
        return true;
    }
}
