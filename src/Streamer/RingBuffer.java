package Streamer;

public class RingBuffer {
    protected volatile int bufferSize = 0;
    protected byte[] buffer = null;
    protected volatile int putHere = 0;
    protected volatile int getHere = 0;
    protected volatile boolean eof = false;
    protected Object signal = new Object();

    public RingBuffer(int size) {
        bufferSize = size;
        buffer = new byte[size];
    }
    
    public int size() {
        return buffer.length;
    }
    
    public void resize(int newSize) {
        if (bufferSize >= newSize) return;
        byte[] newBuffer = new byte[newSize];
        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        buffer = newBuffer;
        bufferSize = newSize;
    }
   
    public int putAvailable() {
        if (putHere == getHere) return bufferSize - 1;
        if (putHere < getHere) return getHere - putHere - 1;
        return bufferSize - (putHere - getHere) - 1;
    }
  
    public void empty() {
        synchronized (signal) {
            putHere = 0;
            getHere = 0;
            signal.notifyAll();
        }
    }
    
    public void put(byte[] data, int offset, int len) {
        if (len == 0) return;
        
        synchronized (signal) {
            while (putAvailable() < len) {
                try { signal.wait(1000); } catch (Exception e) { System.out.println("Put.Signal.wait:" + e); }
            }
            
            if (putHere >= getHere) {
                int l = Math.min(len, bufferSize - putHere);
                System.arraycopy(data, offset, buffer, putHere, l);
                putHere += l;
                if (putHere >= bufferSize) putHere = 0;
                if (len > l) put(data, offset + l, len - l);
            } else {
                int l = Math.min(len, getHere - putHere - 1);
                System.arraycopy(data, offset, buffer, putHere, l);
                putHere += l;
                if (putHere >= bufferSize) putHere = 0;
            }
            signal.notify();
        }
    }
    
    public int getAvailable() {
        if (putHere == getHere) return 0;
        if (putHere < getHere) return bufferSize - (getHere - putHere);
        return putHere - getHere;
    }
 
    public int get(byte[] data, int offset, int len) {
        if (len == 0) return 0;
        int dataLen = 0;
        
        synchronized (signal) {
            // see if we have enough data
            while (getAvailable() <= 0) {
                if (eof) return (-1);
                try { signal.wait(1000); } catch (Exception e) { System.out.println("Get.Signal.wait:" + e); }
            }
            len = Math.min(len, getAvailable());
            
            // copy data
            if (getHere < putHere) {
                int l = Math.min(len, putHere - getHere);
                System.arraycopy(buffer, getHere, data, offset, l);
                getHere += l;
                if (getHere >= bufferSize) getHere = 0;
                dataLen = l;
            } else {
                int l = Math.min(len, bufferSize - getHere);
                System.arraycopy(buffer, getHere, data, offset, l);
                getHere += l;
                if (getHere >= bufferSize) getHere = 0;
                dataLen = l;
                if (len > l) dataLen += get(data, offset + l, len - l);
            }
            signal.notify();
        }
        
        return dataLen;
    }
 
    public boolean isEOF() {
        return eof;
    }
    
    public void setEOF(boolean eof) {
        this.eof = eof;
    }
}

