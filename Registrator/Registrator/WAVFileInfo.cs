using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Runtime.InteropServices;

namespace Registrator
{
    public class WAVFileInfo
    {
        ushort format_;
        ushort channels_;
        uint sampleRate_;
        uint bytesPerSecond_;
        ushort blockAlign_;
        ushort bitsPerSample_;
        ushort extraLength_;
        byte[] extraBytes_;

        uint samples_;
        float timeLength_;
        uint dataSize_;
        string filename_;
        int marker_offset_;
        ushort marker_;


        public int MarkedOffset
        {
            get { return marker_offset_; }
        }

        public ushort Marker
        {
            get { return marker_; }
        }

        public int Channels
        {
            get { return channels_; }
        }
        public uint SampleRate
        {
            get { return sampleRate_; }
        }
        public int BitsPerSample
        {
            get { return bitsPerSample_; }
        }

        public uint Samples
        {
            get { return samples_; }
        }

        public float TimeLength
        {
            get { return timeLength_; }
        }

        public uint DataSize
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

        private bool ReadChunkId(System.IO.BinaryReader br, string str)
        {
            try {
                byte[] b = br.ReadBytes(str.Length);

                for (int i = 0; i < str.Length; ++i) {
                    if (b[i] != str[i]) {
                        return false;
                    }
                }
                return true;
            } catch(Exception e) {
                return false;
            }
        }

        private void ReadFormatChunk(System.IO.BinaryReader br, uint chunk_id, uint chunk_length)
        {
            format_ = br.ReadUInt16();
            if (format_ != 1) {
                throw new Exception("Unsupported WAV format");
            }
            channels_ = br.ReadUInt16();

            sampleRate_ = br.ReadUInt32();
            bytesPerSecond_ = br.ReadUInt32();

            blockAlign_ = br.ReadUInt16();
            bitsPerSample_ = br.ReadUInt16();
            if (chunk_length > 16)
            {
                extraLength_ = br.ReadUInt16();
                if (extraLength_ > 0)
                {
                    extraBytes_ = br.ReadBytes(extraLength_);
                }
            }
        }

        private void ReadDataChunk(System.IO.BinaryReader br, uint chunk_id, uint chunk_length)
        {
            dataSize_ = chunk_length;
            samples_ = dataSize_ / bytesPerSecond_ / channels_;
            timeLength_ = (float)dataSize_ / bytesPerSecond_;
            br.BaseStream.Seek(chunk_length, SeekOrigin.Current);
        }

        private void ReadFactChunk(System.IO.BinaryReader br, uint chunk_id, uint chunk_length)
        {
            if (chunk_length == 8 && ReadChunkId(br, "SRS ")) {
                marker_offset_ = br.ReadInt16();
                if (marker_offset_ >= Math.Round(timeLength_)) {
                    marker_offset_ = -1;
                } else {
                    marker_ = br.ReadUInt16();
                }
            }
        }

        static public void Mark(String filename, ushort offset, ushort marker)
        {
            WAVFileInfo f = new WAVFileInfo(filename);
            if (f.marker_offset_ != -1) {
                throw new Exception("Already marked");
            }

            System.IO.FileStream fileStream = System.IO.File.Open(filename, System.IO.FileMode.Open);
            System.IO.BinaryWriter br = new System.IO.BinaryWriter(fileStream);
            long fs = fileStream.Length;
            br.Seek(0, SeekOrigin.End);
            br.Write((uint)0x74636166);
            br.Write((uint)8);
            br.Write((uint)0x20535253);
            br.Write((ushort)offset);
            br.Write((ushort)marker);

            br.Seek(4, SeekOrigin.Begin);
            br.Write((uint)(fs + 4));
            br.Close();
        }

        public WAVFileInfo(String filename)
        {
            filename_ = filename;
            System.IO.FileStream fileStream = System.IO.File.Open(filename, System.IO.FileMode.Open);
            System.IO.BinaryReader br = new System.IO.BinaryReader(fileStream);

            if (br.ReadUInt32() != 0x46464952) { //RIFF
                throw new Exception();
            }

            uint riff_len = br.ReadUInt32();

            if (br.ReadUInt32() != 0x45564157) { //WAVE
                throw new Exception();
            }
            
            marker_offset_ = -1;
            while (fileStream.Position < fileStream.Length) {
                try
                {
                    Console.WriteLine(String.Format("Pos: {0}  / Size: {1}", fileStream.Position, fileStream.Length));
                    uint chunk_id = br.ReadUInt32();
                    uint chunk_length = br.ReadUInt32();
                    Console.WriteLine(String.Format("Chunk {0:X} length: {1}", chunk_id, chunk_length));

                    switch (chunk_id)
                    {
                        case 0x20746d66:
                            ReadFormatChunk(br, chunk_id, chunk_length); //fmt 
                            break;
                        case 0x61746164:
                            ReadDataChunk(br, chunk_id, chunk_length); //data
                            break;
                        case 0x74636166:
                            ReadFactChunk(br, chunk_id, chunk_length); //fact
                            break;
                        default:
                            fileStream.Seek(chunk_length, SeekOrigin.Current);
                            break;
                    };

                }
                catch (System.IO.EndOfStreamException e)
                {
                    break;
                }
            }
            fileStream.Close();
        }
    }
}
