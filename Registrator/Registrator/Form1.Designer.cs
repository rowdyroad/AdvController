namespace Registrator
{
    partial class Form1
    {
        /// <summary>
        /// Требуется переменная конструктора.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Освободить все используемые ресурсы.
        /// </summary>
        /// <param name="disposing">истинно, если управляемый ресурс должен быть удален; иначе ложно.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Код, автоматически созданный конструктором форм Windows

        /// <summary>
        /// Обязательный метод для поддержки конструктора - не изменяйте
        /// содержимое данного метода при помощи редактора кода.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
            this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.обновитьToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
            this.переименоватьToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.удалитьToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.logArea = new System.Windows.Forms.RichTextBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.ident = new System.Windows.Forms.Label();
            this.fileInfo = new System.Windows.Forms.Label();
            this.backup = new System.Windows.Forms.CheckBox();
            this.replace = new System.Windows.Forms.CheckBox();
            this.dojob = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.filename = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.name = new System.Windows.Forms.TextBox();
            this.browse = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.promoList = new System.Windows.Forms.ListBox();
            this.rident = new System.Windows.Forms.Label();
            this.contextMenuStrip1.SuspendLayout();
            this.panel1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // openFileDialog1
            // 
            this.openFileDialog1.FileName = "openFileDialog1";
            this.openFileDialog1.Filter = "WAV файлы (*.wav)|*.wav|Все файлы (*.*)|*.*";
            // 
            // contextMenuStrip1
            // 
            this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.обновитьToolStripMenuItem,
            this.toolStripMenuItem1,
            this.переименоватьToolStripMenuItem,
            this.удалитьToolStripMenuItem});
            this.contextMenuStrip1.Name = "contextMenuStrip1";
            this.contextMenuStrip1.Size = new System.Drawing.Size(162, 76);
            // 
            // обновитьToolStripMenuItem
            // 
            this.обновитьToolStripMenuItem.Name = "обновитьToolStripMenuItem";
            this.обновитьToolStripMenuItem.Size = new System.Drawing.Size(161, 22);
            this.обновитьToolStripMenuItem.Text = "Обновить";
            this.обновитьToolStripMenuItem.Click += new System.EventHandler(this.обновитьToolStripMenuItem_Click);
            // 
            // toolStripMenuItem1
            // 
            this.toolStripMenuItem1.Name = "toolStripMenuItem1";
            this.toolStripMenuItem1.Size = new System.Drawing.Size(158, 6);
            // 
            // переименоватьToolStripMenuItem
            // 
            this.переименоватьToolStripMenuItem.Name = "переименоватьToolStripMenuItem";
            this.переименоватьToolStripMenuItem.Size = new System.Drawing.Size(161, 22);
            this.переименоватьToolStripMenuItem.Text = "Переименовать";
            this.переименоватьToolStripMenuItem.Click += new System.EventHandler(this.переименоватьToolStripMenuItem_Click);
            // 
            // удалитьToolStripMenuItem
            // 
            this.удалитьToolStripMenuItem.Name = "удалитьToolStripMenuItem";
            this.удалитьToolStripMenuItem.Size = new System.Drawing.Size(161, 22);
            this.удалитьToolStripMenuItem.Text = "Удалить";
            this.удалитьToolStripMenuItem.Click += new System.EventHandler(this.удалитьToolStripMenuItem_Click);
            // 
            // logArea
            // 
            this.logArea.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.logArea.Location = new System.Drawing.Point(0, 232);
            this.logArea.Name = "logArea";
            this.logArea.ReadOnly = true;
            this.logArea.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.ForcedVertical;
            this.logArea.Size = new System.Drawing.Size(668, 84);
            this.logArea.TabIndex = 4;
            this.logArea.Text = "";
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.groupBox2);
            this.panel1.Controls.Add(this.groupBox1);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(668, 232);
            this.panel1.TabIndex = 5;
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.rident);
            this.groupBox2.Controls.Add(this.ident);
            this.groupBox2.Controls.Add(this.fileInfo);
            this.groupBox2.Controls.Add(this.backup);
            this.groupBox2.Controls.Add(this.replace);
            this.groupBox2.Controls.Add(this.dojob);
            this.groupBox2.Controls.Add(this.label2);
            this.groupBox2.Controls.Add(this.filename);
            this.groupBox2.Controls.Add(this.label1);
            this.groupBox2.Controls.Add(this.name);
            this.groupBox2.Controls.Add(this.browse);
            this.groupBox2.Dock = System.Windows.Forms.DockStyle.Fill;
            this.groupBox2.Location = new System.Drawing.Point(300, 0);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(368, 232);
            this.groupBox2.TabIndex = 5;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Добавление нового ролика";
            this.groupBox2.Enter += new System.EventHandler(this.groupBox2_Enter);
            // 
            // ident
            // 
            this.ident.AutoSize = true;
            this.ident.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.ident.Location = new System.Drawing.Point(9, 74);
            this.ident.Name = "ident";
            this.ident.Size = new System.Drawing.Size(0, 13);
            this.ident.TabIndex = 9;
            // 
            // fileInfo
            // 
            this.fileInfo.AutoSize = true;
            this.fileInfo.Location = new System.Drawing.Point(9, 57);
            this.fileInfo.Name = "fileInfo";
            this.fileInfo.Size = new System.Drawing.Size(0, 13);
            this.fileInfo.TabIndex = 8;
            // 
            // backup
            // 
            this.backup.AutoSize = true;
            this.backup.Checked = true;
            this.backup.CheckState = System.Windows.Forms.CheckState.Checked;
            this.backup.Location = new System.Drawing.Point(9, 174);
            this.backup.Name = "backup";
            this.backup.Size = new System.Drawing.Size(296, 17);
            this.backup.TabIndex = 7;
            this.backup.Text = "Сохранять резервную копию исходного файла (*.bkp)";
            this.backup.UseVisualStyleBackColor = true;
            // 
            // replace
            // 
            this.replace.AutoSize = true;
            this.replace.Checked = true;
            this.replace.CheckState = System.Windows.Forms.CheckState.Checked;
            this.replace.Location = new System.Drawing.Point(9, 151);
            this.replace.Name = "replace";
            this.replace.Size = new System.Drawing.Size(157, 17);
            this.replace.TabIndex = 7;
            this.replace.Text = "Заменять исходный файл";
            this.replace.UseVisualStyleBackColor = true;
            this.replace.CheckedChanged += new System.EventHandler(this.replace_CheckedChanged);
            // 
            // dojob
            // 
            this.dojob.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dojob.Location = new System.Drawing.Point(229, 197);
            this.dojob.Name = "dojob";
            this.dojob.Size = new System.Drawing.Size(127, 23);
            this.dojob.TabIndex = 6;
            this.dojob.Text = "Добавить";
            this.dojob.UseVisualStyleBackColor = true;
            this.dojob.Click += new System.EventHandler(this.button2_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 16);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(75, 13);
            this.label2.TabIndex = 4;
            this.label2.Text = "Файл ролика";
            // 
            // filename
            // 
            this.filename.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.filename.BackColor = System.Drawing.SystemColors.Window;
            this.filename.Location = new System.Drawing.Point(9, 32);
            this.filename.Name = "filename";
            this.filename.ReadOnly = true;
            this.filename.Size = new System.Drawing.Size(347, 20);
            this.filename.TabIndex = 3;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(6, 109);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(96, 13);
            this.label1.TabIndex = 2;
            this.label1.Text = "Название ролика";
            // 
            // name
            // 
            this.name.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.name.Location = new System.Drawing.Point(9, 125);
            this.name.Name = "name";
            this.name.Size = new System.Drawing.Size(347, 20);
            this.name.TabIndex = 1;
            // 
            // browse
            // 
            this.browse.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.browse.Location = new System.Drawing.Point(281, 57);
            this.browse.Name = "browse";
            this.browse.Size = new System.Drawing.Size(75, 23);
            this.browse.TabIndex = 0;
            this.browse.Text = "Обзор...";
            this.browse.UseVisualStyleBackColor = true;
            this.browse.Click += new System.EventHandler(this.button1_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.promoList);
            this.groupBox1.Dock = System.Windows.Forms.DockStyle.Left;
            this.groupBox1.Location = new System.Drawing.Point(0, 0);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(300, 232);
            this.groupBox1.TabIndex = 3;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Ролики";
            // 
            // promoList
            // 
            this.promoList.ContextMenuStrip = this.contextMenuStrip1;
            this.promoList.Dock = System.Windows.Forms.DockStyle.Fill;
            this.promoList.FormattingEnabled = true;
            this.promoList.Location = new System.Drawing.Point(3, 16);
            this.promoList.Name = "promoList";
            this.promoList.Size = new System.Drawing.Size(294, 212);
            this.promoList.Sorted = true;
            this.promoList.TabIndex = 1;
            // 
            // rident
            // 
            this.rident.AutoSize = true;
            this.rident.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.rident.Location = new System.Drawing.Point(9, 197);
            this.rident.Name = "rident";
            this.rident.Size = new System.Drawing.Size(0, 13);
            this.rident.TabIndex = 10;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSize = true;
            this.ClientSize = new System.Drawing.Size(668, 316);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.logArea);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
            this.Name = "Form1";
            this.Text = "Регистратор";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.contextMenuStrip1.ResumeLayout(false);
            this.panel1.ResumeLayout(false);
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.OpenFileDialog openFileDialog1;
        private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
        private System.Windows.Forms.ToolStripMenuItem обновитьToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem переименоватьToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem удалитьToolStripMenuItem;
        private System.Windows.Forms.RichTextBox logArea;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.ListBox promoList;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox filename;
        private System.Windows.Forms.Button browse;
        private System.Windows.Forms.CheckBox backup;
        private System.Windows.Forms.CheckBox replace;
        private System.Windows.Forms.Button dojob;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox name;
        private System.Windows.Forms.Label ident;
        private System.Windows.Forms.Label fileInfo;
        private System.Windows.Forms.Label rident;
    }
}

