using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using System.Reflection;

namespace Registrator
{
    public partial class Form1 : Form
    {
        private WAVFileInfo current_file = null;
        private ProcessStartInfo watermarker_ = null;
        private ProcessStartInfo detector_ = null;

        private void showError(string msg)
        {
            MessageBox.Show(msg, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
        private void loadIdentList()
        {
            dataGridView1.Rows.Clear();

            log("Обновление списка роликов...");
            PromoIndentList idents  = Loader.Load<PromoIndentList>("identsall");
            if (idents.result == "success")
            {
                for (int i = 0; i < idents.data.Length; ++i) {
                    dataGridView1.Rows.Add(idents.data[i].Ident, idents.data[i].Name, idents.data[i].Add_time);
                    dataGridView1.Rows[i].Tag = idents.data[i];
                    if (idents.data[i].Actived == 0)
                    {
                        for (int j = 0; j < dataGridView1.Rows[i].Cells.Count; ++j)
                        {
                            dataGridView1.Rows[i].Cells[j].Style.ForeColor = Color.Silver;
                        }
                    }
                }
                log(String.Format("Загружено {0} роликов", idents.data.Length));
            }
            else
            {
                log("Ошибка при загрузке списка роликов");
            }

            comboBox1_SelectedIndexChanged(null, null);
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
                    showError("Неверный формат файла!");
                    return;
                }
                filename.Text = openFileDialog1.FileName;
                fileInfo.Text = String.Format("{3:F3} сек ({0} Гц / {2} {1})", current_file.SampleRate, ((float)current_file.Channels == 1) ? "Моно" : "Стерео", current_file.BitsPerSample, current_file.TimeLength);
                if (getFileIdent(filename.Text, ident) == 0)
                {
                    dojob.Enabled = true;
                }


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
            comboBox1.SelectedIndex = 0;
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
                showError("Не задано название файла");
                return;
            }

            dojob.Enabled = false;

            int samples = current_file.Samples;
            int freq = current_file.SampleRate;
            int length = (int)Math.Floor(current_file.TimeLength);
            log("Получение идентификатора ролика...");
            IdentResult r = Loader.Load<IdentResult>("getalways");
            if (r.result == "success")
            {
                if (r.actived == 0)
                {
                    if (MessageBox.Show("В системе отсутствуют свободные идентификаторы ролика. Ролик будет добавлен с дублирвоанием идентификатора в деактивированном состоянии. Продолжить?", "Добавление ролика", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No)
                    {

                        dojob.Enabled = true;
                        return;
                    }
                }
                log(String.Format("Идентификатор ролика {0}", r.ident));                
                log("Маркирование ролика "+filename.Text+"...");
                
                string fn = filename.Text;
                string res = getFreeFilename(fn);
                watermarker_.Arguments = "\"" + fn + "\" \"" + res + "\" " + String.Format("{0:X2}", r.ident) + " 2 0";
               
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
                            log(fn);
                            string fn_bkp = fn.Substring(0, fn.LastIndexOf(".")) + ".bkp.wav";
                            if (File.Exists(fn_bkp))
                            {
                                fn_bkp = getFreeFilename(fn_bkp);
                            }
                            log("Сохранение резервной копии исходного файла " + fn_bkp);
                            File.Move(fn,fn_bkp);
                        }
                        File.Move(res, fn);
                        res = fn;
                    }
                    log("Результат сохранен в файл " + res);
                    log("Регистрация ролика на сервере...");
                    Result m = Loader.Load<Result>("add&ident=" + r.ident + "&length=" + length + "&name=" + System.Web.HttpUtility.UrlEncode(name.Text)+"&actived="+r.actived);
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
      
        private void button1_Click_1(object sender, EventArgs e)
        {
            loadIdentList();
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            Dictionary<int, int> z = new Dictionary<int, int>();

            for (int i = 0; i < dataGridView1.Rows.Count; ++i)
            {
                dataGridView1.Rows[i].Visible = true;
                int count;
                int ident = (dataGridView1.Rows[i].Tag as PromoIdent).Ident;
                if (z.TryGetValue(ident, out count))
                {
                    ++count;
                }
                else
                {
                    count = 1;
                }
                z[ident] = count;
            }

            int k = dataGridView1.Rows.Count;

            switch(comboBox1.SelectedIndex) {
                case 1:
                case 2:
                    for (int i = 0; i < dataGridView1.Rows.Count; ++i)
                    {                       
                        dataGridView1.Rows[i].Visible = ((comboBox1.SelectedIndex - 1) == (dataGridView1.Rows[i].Tag as PromoIdent).Actived);
                        if (!dataGridView1.Rows[i].Visible)
                        {
                            --k;
                        }
                    }
                break;

                case 3:
                    for (int i = 0; i < dataGridView1.Rows.Count; ++i)
                    {
                        int ident = (dataGridView1.Rows[i].Tag as PromoIdent).Ident;                       
                        dataGridView1.Rows[i].Visible = z[ident] > 1;
                        if (!dataGridView1.Rows[i].Visible)
                        {
                            --k;
                        }
                    }
                break;
            }

            label5.Text = k.ToString();
        }

        private void dataGridView1_CellEndEdit(object sender, DataGridViewCellEventArgs e)
        {
            DataGridViewRow row = dataGridView1.Rows[e.RowIndex];
            PromoIdent ident = (PromoIdent)row.Tag;
            
            if (e.ColumnIndex == 1) {
                    if (row.Cells[e.ColumnIndex].Value == ident.Name) return;

                    if (MessageBox.Show("Переименовать ролик '" + ident.Name + "' в '" + row.Cells[e.ColumnIndex].Value + "'", "Переименование ролика", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                    {
                        log("Переименование ролика '" + ident.Name + "' в '" + row.Cells[e.ColumnIndex].Value + "'");
                        Result r = Loader.Load<Result>("rename&id=" + ident.Id.ToString() + "&name=" + row.Cells[e.ColumnIndex].Value);
                        if (r.result == "success")
                        {
                            log("Ролик успешно переименован");
                            ident.Name = (string)row.Cells[e.ColumnIndex].Value;
                            return;
                        }
                    }
                    row.Cells[e.ColumnIndex].Value = ident.Name;                                   
            }
        }

        private void dataGridView1_CellClick(object sender, DataGridViewCellEventArgs e)
        {            
        }

        private void button2_Click_1(object sender, EventArgs e)
        {            
            if (dataGridView1.SelectedCells.Count > 0)
            {
                int ri = dataGridView1.SelectedCells[0].RowIndex;
                PromoIdent pi = (PromoIdent)dataGridView1.Rows[ri].Tag;
                if (MessageBox.Show("Удалить ролик '" + pi.Name+"'", "Удаление ролика", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) == DialogResult.Yes)
                {
                    log("Удаление ролика '"+pi.Name+"'");
                    Result res = Loader.Load<Result>("delete&id=" + pi.Id.ToString());
                    if (res.result == "success")
                    {
                        log("Ролик успешно удален");
                        dataGridView1.Rows.RemoveAt(ri);
                        label5.Text = (int.Parse(label5.Text) - 1).ToString();
                    }
                }
            }
        }

        private void dataGridView1_SelectionChanged(object sender, EventArgs e)
        {
           
        }

        private void dataGridView1_CellClick_1(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex < 0) return;
            bool actived = ((dataGridView1.Rows[e.RowIndex].Tag as PromoIdent).Actived == 1);
            button3.Enabled = !actived;
            button4.Enabled = actived;
            button2.Enabled = true;
        }

        private void changeStatus(string caption, string command, int actived, Color successColor)
        {
            if (dataGridView1.SelectedCells.Count > 0)
            {
                int ri = dataGridView1.SelectedCells[0].RowIndex;
                PromoIdent pi = (PromoIdent)dataGridView1.Rows[ri].Tag;

                if (MessageBox.Show(caption + " ролик '" + pi.Name + "'", "Смена статуса ролика", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
                {
                    log("Передача команды '" + caption + "' для ролика '" + pi.Name + "'");
                    Result r = Loader.Load<Result>(command + "&id=" + pi.Id.ToString());
                    if (r.result == "success")
                    {
                        log("Команда успешно выполнена");
                        pi.Actived = actived;
                        for (int j = 0; j < dataGridView1.Rows[ri].Cells.Count; ++j)
                        {
                            dataGridView1.Rows[ri].Cells[j].Style.ForeColor = successColor;
                        }

                        dataGridView1_CellClick_1(null, new DataGridViewCellEventArgs(0,ri));
                    }

                    if (r.result == "exists")
                    {                        
                        showError("Данный идентификатор активирован для другого ролика. Вначале деактивируйте другой ролик.");
                    }
                }
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            changeStatus("Активировать", "activate", 1, Color.Black);
        }

        private void button4_Click(object sender, EventArgs e)
        {
            changeStatus("Деактивировать", "deactivate", 0, Color.Silver);
        }

        
    }
}
