using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using Registrator.WAV;
using System.Reflection;

namespace Registrator
{
    public partial class Form1 : Form
    {
        private WAVFileInfo current_file = null;
        private ProcessStartInfo watermarker_ = null;
        private ProcessStartInfo detector_ = null;

        
        private void loadIdentList()
        {
            promoList.Items.Clear();

            log("Обновление списка роликов...");
            PromoIndentList idents  = Loader.Load<PromoIndentList>("idents");
            if (idents.result == "success")
            {
                for (int i = 0; i < idents.data.Length; ++i)
                {
                    promoList.Items.Add(idents.data[i]);
                }
                log(String.Format("Загружено {0} роликов", idents.data.Length));
            }
            else
            {
                log("Ошибка при загрузке списка роликов");
            }
        }
        public Form1(ProcessStartInfo watermarker, ProcessStartInfo detector)
        {
            watermarker_ = watermarker;
            detector_ = detector;
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                current_file = new WAVFileInfo(openFileDialog1.FileName);
                if (current_file.Samples <= 0 || current_file.SampleRate <= 0)
                {
                    current_file = null;
                    fileInfo.Text = "";
                    MessageBox.Show("Неверный формат файла!");
                    return;
                }
                filename.Text = openFileDialog1.FileName;
                fileInfo.Text = String.Format("{3:F3} сек ({0} Гц / {2} {1})", current_file.SampleRate, ((float)current_file.Channels == 1) ? "Моно" : "Стерео", current_file.BitsPerSample, current_file.TimeLength);
                int i = getFileIdent(filename.Text,ident);


            }
        }

        private void groupBox3_Enter(object sender, EventArgs e)
        {

        }

        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            loadIdentList();
        }

        private void обновитьToolStripMenuItem_Click(object sender, EventArgs e)
        {
            loadIdentList();
        }

        private void удалитьToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (promoList.SelectedIndex == -1) return;
            PromoIdent ident = (PromoIdent)promoList.SelectedItem;
            log(String.Format("Удаление ролика '{0}'",ident.Name));
            Loader.Load<Result>("delete&id="+ident.Id);
            loadIdentList();
        }

        private void log(Object obj)
        {
            logArea.Text += String.Format("[{0}] {1}\r\n", System.DateTime.Now.ToLongTimeString(), obj);

            logArea.SelectionStart = logArea.Text.Length;
            logArea.ScrollToCaret();
        }

        private String getFreeFilename(String filename)
        {
            int i = filename.LastIndexOf(".");
            if (i == -1) i = filename.Length;
            string basename = filename.Substring(0,i);
            string ext = filename.Substring(i + 1, filename.Length - (i+1));
            for (int j = 1; ; ++j)
            {
                String new_fn = String.Format("{0}({1}).{2}",basename,j,ext);
                if (!File.Exists(new_fn))
                {
                    return new_fn;
                }
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (name.Text.Trim().Length == 0)
            {
                MessageBox.Show("Не задано название файла");
                return;
            }
            if (current_file == null)
            {
                MessageBox.Show("Файл не выбран");
                return;
            }

            int samples = current_file.Samples;
            int freq = current_file.SampleRate;
            int length = (int)Math.Floor(current_file.TimeLength);
            log("Получение идентификатора ролика...");
            IdentResult r = Loader.Load<IdentResult>("get&length="+length);
            if (r.result == "success")
            {
                log(String.Format("Идентификатор ролика {0}", r.ident));                
                log("Маркирование ролика "+filename.Text+"...");
                
                string fn = filename.Text;
                string res = getFreeFilename(fn);
                watermarker_.Arguments = "\""+filename.Text + "\" \"" + res + "\" " + String.Format("{0:X2}", r.ident);

                Process p = Process.Start(watermarker_);
                p.WaitForExit();
                if (p.ExitCode == 0)
                {
                    int i = getFileIdent(res,rident);

                    if (i != r.ident)
                    {
                        MessageBox.Show("Ошибка при маркировании ролика. Идентификаторы не совпадают", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        File.Delete(res);
                        return;
                    }

                    log("Маркирование ролика завершено успешно");
                    if (replace.Checked)
                    {
                        if (backup.Checked)
                        {
                            string fn_bkp = getFreeFilename(fn + ".bkp");
                            log("Сохранение резервной копии исходного файла " + fn_bkp);
                            File.Move(fn,fn_bkp);
                        }
                        File.Move(res, fn);
                        res = fn;
                    }
                    log("Результат сохранен в файл " + res);
                    log("Регистрация ролика на сервере...");
                    Result m = Loader.Load<Result>("add&ident=" + r.ident + "&length=" + length + "&name=" + System.Web.HttpUtility.UrlEncode(name.Text));
                    if (m.result == "success")
                    {
                        log("Ролик успешно зарегистрирован");
                        loadIdentList();
                        if (MessageBox.Show("Результат сохранен в файл " + res + ".\r\nОткрыть папку с файлом?", this.Text, MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                        {
                            Process.Start(new FileInfo(res).Directory.FullName);
                        }
                    }
                    else
                    {
                        log("Ошибка регистрации ролика на сервере");
                    }
                }
                else
                {
                    log("Ошибка при маркировании ролика "+ p.ExitCode);
                }
            }
            else
            {
                log("Ошибка при получении идентификатора ролика");
            }
        }
        
        private void replace_CheckedChanged(object sender, EventArgs e)
        {
            backup.Checked = replace.Checked;
            backup.Enabled = replace.Checked;
        }
       
        private void переименоватьToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (promoList.SelectedIndex == -1) return;
            String old_name = ((PromoIdent)promoList.SelectedItem).Name;
            if (new RenameForm((PromoIdent)promoList.SelectedItem).ShowDialog() == DialogResult.OK)
            {
                log(String.Format("Ролик '{0}' переименован в '{1}'", old_name, ((PromoIdent)promoList.SelectedItem).Name));
                loadIdentList();
            }

        }

        private int getFileIdent(String filename, Label label)
        {
            log("Проверка файла \"" + filename + "\" на наличие метки...");
            label.Text = "Идет проверка наличия метки...";
            detector_.Arguments = "\"" + filename + "\"";
            Process p = Process.Start(detector_);
            while (true)
            {
                this.Refresh();
                String r = p.StandardOutput.ReadLine();
                if (r == null) break;
                if (r[0] == '+')
                {
                    String[] data = r.Split(' ');
                    int z = 0;
                    if (int.TryParse(data[2], out z))
                    {
                        label.Text = "Ролик содержит метку: " + z.ToString();
                        log("Найдена метка: " + z.ToString());
                        return z;
                    }
                }
            }
            label.Text = "Ролик не содержит меток";
            log("Метки не найдены");
            return 0;
        }

        private void groupBox2_Enter(object sender, EventArgs e)
        {

        }
    }
}
