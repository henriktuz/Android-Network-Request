package com.tuz.network;

/**
 * Class representing the current transfer progress.
 */
public final class TransferProgress {

    /**
     * The total bytes transferred.
     */
    private long mTotalBytes;

    /**
     * The number of bytes transferred.
     */
    private long mBytesTransferred;

    /**
     * Update the current progress.
     *
     * @param totalBytes the total bytes transferred.
     * @param bytesTransferred the bytes transferred.
     * @return true if the percent has changed, otherwise false.
     */
    boolean update(long totalBytes, long bytesTransferred) {
        int current = getPercent();
        mTotalBytes = totalBytes;
        mBytesTransferred = bytesTransferred;
        return current != getPercent();
    }

    /**
     * Get the total number of bytes to transfer.
     *
     * @return the byte size.
     */
    public long getTotalBytes() {
        return mTotalBytes;
    }

    /**
     * Get the number of bytes transferred.
     *
     * @return the byte size.
     */
    public long getTransferredBytes() {
        return mBytesTransferred;
    }

    /**
     * Get the percent transferred.
     *
     * @return the percent.
     */
    public int getPercent() {
        int percent = 0;

        if (mTotalBytes > 0) {
            percent = (int) (100 * mBytesTransferred / mTotalBytes);
        }

        return percent;
    }
}
