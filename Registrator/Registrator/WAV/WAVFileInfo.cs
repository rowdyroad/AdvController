using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Runtime.InteropServices;

namespace Registrator.WAV
{
    public class WAVFileInfo
    {
        int channels_;
        int bitsPerSample_;
        int sampleRate_;
        int bytesPerSecond_;
        int samples_;
        float timeLength_;
        int dataSize_;
        string filename_;

        public int Channels
        {
            get { return channels_; }
        }
        public int SampleRate
        {
            get { return sampleRate_; }
        }
        public int BitsPerSample
        {
            get { return bitsPerSample_; }
        }

        public int Samples
        {
            get { return samples_; }
        }

        public float TimeLength
        {
            get { return timeLength_; }
        }

        public int DataSize
        {
            get { return dataSize_; }
        }

        String Filename
        {
            get { return filename_; }
        }

        private void waitFor(FileStream fileStream, String tag)
        {
            byte[] buf = new byte[4];
            while (true)
            {
                int len = fileStream.Read(buf, 0, buf.Length);
                String str = "";
                for (int i = 0; i < len; ++i)
                {
                    str += (char)buf[i];
                }
                if (str == tag)
                {
                    break;
                }
                int length = fileStream.ReadByte() | fileStream.ReadByte() << 8 | fileStream.ReadByte() << 16 | fileStream.ReadByte() << 24;
                if (length == -1) throw new Exception();
                fileStream.Seek(length, SeekOrigin.Current);
            }
        }

        public WAVFileInfo(String filename)
        {
            filename_ = filename;
            System.IO.FileStream fileStream = System.IO.File.Open(filename, System.IO.FileMode.Open);
            if (
                fileStream.ReadByte() != 'R' ||
                fileStream.ReadByte() != 'I' ||
                fileStream.ReadByte() != 'F' ||
                fileStream.ReadByte() != 'F'
               ) throw new Exception();

            Console.WriteLine(fileStream.Position);
            fileStream.Seek(4, SeekOrigin.Current);
            Console.WriteLine(fileStream.Position);
            
            if (
                   fileStream.ReadByte() != 'W' ||
                   fileStream.ReadByte() != 'A' ||
                   fileStream.ReadByte() != 'V' ||
                   fileStream.ReadByte() != 'E'
            ) throw new Exception();

            waitFor(fileStream, "fmt "); 
            int len = fileStream.ReadByte() | fileStream.ReadByte() << 8 | fileStream.ReadByte() << 16 | fileStream.ReadByte() << 24;

            int pos = (int)fileStream.Position;
            short format = (short)(fileStream.ReadByte() | fileStream.ReadByte() << 8);
            channels_ = fileStream.ReadByte() | fileStream.ReadByte() << 8;
            sampleRate_ = fileStream.ReadByte() | fileStream.ReadByte() << 8 | fileStream.ReadByte() << 16 | fileStream.ReadByte() << 24;
            bytesPerSecond_ = fileStream.ReadByte() | fileStream.ReadByte() << 8 | fileStream.ReadByte() << 16 | fileStream.ReadByte() << 24;
            short blockalign = (short)(fileStream.ReadByte() | fileStream.ReadByte() << 8);
            bitsPerSample_ = fileStream.ReadByte() | fileStream.ReadByte() << 8;
            fileStream.Seek(len - (fileStream.Position - pos),SeekOrigin.Current);

            waitFor(fileStream, "data");
            dataSize_ = fileStream.ReadByte() | fileStream.ReadByte() << 8 | fileStream.ReadByte() << 16 | fileStream.ReadByte() << 24;
            samples_ = dataSize_ / bytesPerSecond_ / channels_;
            timeLength_ = (float)dataSize_ / bytesPerSecond_;
            fileStream.Close();
        }
    }
}
