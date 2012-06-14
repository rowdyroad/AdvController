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
            this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
            this.logArea = new System.Windows.Forms.RichTextBox();
            this.panel1 = new System.Windows.Forms.Panel();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.offsetLabel = new System.Windows.Forms.Label();
            this.offset = new System.Windows.Forms.TrackBar();
            this.rident = new System.Windows.Forms.Label();
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
            this.panel3 = new System.Windows.Forms.Panel();
            this.dataGridView1 = new System.Windows.Forms.DataGridView();
            this.IdColumn = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.NameColumn = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.TimeColumn = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.panel4 = new System.Windows.Forms.Panel();
            this.button4 = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.button3 = new System.Windows.Forms.Button();
            this.panel2 = new System.Windows.Forms.Panel();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.comboBox1 = new System.Windows.Forms.ComboBox();
            this.label3 = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.panel1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.offset)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.panel3.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).BeginInit();
            this.panel4.SuspendLayout();
            this.panel2.SuspendLayout();
            this.SuspendLayout();
            // 
            // openFileDialog1
            // 
            this.openFileDialog1.FileName = "openFileDialog1";
            this.openFileDialog1.Filter = "WAV файлы (*.wav)|*.wav|Все файлы (*.*)|*.*";
            // 
            // logArea
            // 
            this.logArea.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.logArea.Location = new System.Drawing.Point(0, 372);
            this.logArea.Name = "logArea";
            this.logArea.ReadOnly = true;
            this.logArea.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.ForcedVertical;
            this.logArea.Size = new System.Drawing.Size(924, 84);
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
            this.panel1.Size = new System.Drawing.Size(924, 372);
            this.panel1.TabIndex = 5;
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.offsetLabel);
            this.groupBox2.Controls.Add(this.offset);
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
            this.groupBox2.Location = new System.Drawing.Point(613, 0);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(311, 372);
            this.groupBox2.TabIndex = 5;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Добавление нового ролика";
            this.groupBox2.Enter += new System.EventHandler(this.groupBox2_Enter);
            // 
            // offsetLabel
            // 
            this.offsetLabel.AutoSize = true;
            this.offsetLabel.Location = new System.Drawing.Point(9, 194);
            this.offsetLabel.Name = "offsetLabel";
            this.offsetLabel.Size = new System.Drawing.Size(129, 13);
            this.offsetLabel.TabIndex = 12;
            this.offsetLabel.Text = "Отступ от начал файла: ";
            // 
            // offset
            // 
            this.offset.Enabled = false;
            this.offset.Location = new System.Drawing.Point(6, 210);
            this.offset.Name = "offset";
            this.offset.Size = new System.Drawing.Size(292, 45);
            this.offset.TabIndex = 11;
            this.offset.Scroll += new System.EventHandler(this.offset_Scroll);
            // 
            // rident
            // 
            this.rident.AutoSize = true;
            this.rident.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.rident.Location = new System.Drawing.Point(9, 242);
            this.rident.Name = "rident";
            this.rident.Size = new System.Drawing.Size(0, 13);
            this.rident.TabIndex = 10;
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
            this.dojob.Enabled = false;
            this.dojob.Location = new System.Drawing.Point(6, 324);
            this.dojob.Name = "dojob";
            this.dojob.Size = new System.Drawing.Size(290, 39);
            this.dojob.TabIndex = 6;
            this.dojob.Text = "Маркировать и добавить";
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
            this.filename.Size = new System.Drawing.Size(290, 20);
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
            this.name.Size = new System.Drawing.Size(290, 20);
            this.name.TabIndex = 1;
            // 
            // browse
            // 
            this.browse.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.browse.Location = new System.Drawing.Point(224, 57);
            this.browse.Name = "browse";
            this.browse.Size = new System.Drawing.Size(75, 23);
            this.browse.TabIndex = 0;
            this.browse.Text = "Обзор...";
            this.browse.UseVisualStyleBackColor = true;
            this.browse.Click += new System.EventHandler(this.button1_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.panel3);
            this.groupBox1.Controls.Add(this.panel2);
            this.groupBox1.Dock = System.Windows.Forms.DockStyle.Left;
            this.groupBox1.Location = new System.Drawing.Point(0, 0);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(613, 372);
            this.groupBox1.TabIndex = 3;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Ролики";
            // 
            // panel3
            // 
            this.panel3.Controls.Add(this.dataGridView1);
            this.panel3.Controls.Add(this.panel4);
            this.panel3.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel3.Location = new System.Drawing.Point(3, 52);
            this.panel3.Name = "panel3";
            this.panel3.Size = new System.Drawing.Size(607, 317);
            this.panel3.TabIndex = 4;
            // 
            // dataGridView1
            // 
            this.dataGridView1.AllowUserToAddRows = false;
            this.dataGridView1.AllowUserToDeleteRows = false;
            this.dataGridView1.AllowUserToResizeRows = false;
            this.dataGridView1.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridView1.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.IdColumn,
            this.NameColumn,
            this.TimeColumn});
            this.dataGridView1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dataGridView1.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
            this.dataGridView1.Location = new System.Drawing.Point(0, 0);
            this.dataGridView1.MultiSelect = false;
            this.dataGridView1.Name = "dataGridView1";
            this.dataGridView1.ShowEditingIcon = false;
            this.dataGridView1.Size = new System.Drawing.Size(607, 282);
            this.dataGridView1.TabIndex = 5;
            this.dataGridView1.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dataGridView1_CellClick_1);
            this.dataGridView1.CellEndEdit += new System.Windows.Forms.DataGridViewCellEventHandler(this.dataGridView1_CellEndEdit);
            this.dataGridView1.SelectionChanged += new System.EventHandler(this.dataGridView1_SelectionChanged);
            // 
            // IdColumn
            // 
            this.IdColumn.HeaderText = "Id";
            this.IdColumn.Name = "IdColumn";
            this.IdColumn.ReadOnly = true;
            this.IdColumn.Width = 60;
            // 
            // NameColumn
            // 
            this.NameColumn.HeaderText = "Название";
            this.NameColumn.Name = "NameColumn";
            this.NameColumn.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.NameColumn.Width = 240;
            // 
            // TimeColumn
            // 
            this.TimeColumn.HeaderText = "Время добавления";
            this.TimeColumn.Name = "TimeColumn";
            this.TimeColumn.ReadOnly = true;
            this.TimeColumn.Width = 140;
            // 
            // panel4
            // 
            this.panel4.Controls.Add(this.button4);
            this.panel4.Controls.Add(this.button2);
            this.panel4.Controls.Add(this.button3);
            this.panel4.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panel4.Location = new System.Drawing.Point(0, 282);
            this.panel4.Name = "panel4";
            this.panel4.Size = new System.Drawing.Size(607, 35);
            this.panel4.TabIndex = 4;
            // 
            // button4
            // 
            this.button4.Enabled = false;
            this.button4.Location = new System.Drawing.Point(128, 6);
            this.button4.Name = "button4";
            this.button4.Size = new System.Drawing.Size(113, 23);
            this.button4.TabIndex = 3;
            this.button4.Text = "Деактивировать";
            this.button4.UseVisualStyleBackColor = true;
            this.button4.Click += new System.EventHandler(this.button4_Click);
            // 
            // button2
            // 
            this.button2.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button2.Enabled = false;
            this.button2.Location = new System.Drawing.Point(484, 6);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(113, 23);
            this.button2.TabIndex = 1;
            this.button2.Text = "Удалить";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click_1);
            // 
            // button3
            // 
            this.button3.Enabled = false;
            this.button3.Location = new System.Drawing.Point(9, 6);
            this.button3.Name = "button3";
            this.button3.Size = new System.Drawing.Size(113, 23);
            this.button3.TabIndex = 2;
            this.button3.Text = "Активировать";
            this.button3.UseVisualStyleBackColor = true;
            this.button3.Click += new System.EventHandler(this.button3_Click);
            // 
            // panel2
            // 
            this.panel2.Controls.Add(this.label5);
            this.panel2.Controls.Add(this.label4);
            this.panel2.Controls.Add(this.comboBox1);
            this.panel2.Controls.Add(this.label3);
            this.panel2.Controls.Add(this.button1);
            this.panel2.Dock = System.Windows.Forms.DockStyle.Top;
            this.panel2.Location = new System.Drawing.Point(3, 16);
            this.panel2.Name = "panel2";
            this.panel2.Size = new System.Drawing.Size(607, 36);
            this.panel2.TabIndex = 0;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(481, 12);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(0, 13);
            this.label5.TabIndex = 4;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(361, 12);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(114, 13);
            this.label4.TabIndex = 3;
            this.label4.Text = "Элементов в списке:";
            // 
            // comboBox1
            // 
            this.comboBox1.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboBox1.FormattingEnabled = true;
            this.comboBox1.Items.AddRange(new object[] {
            "Все",
            "Неактивные",
            "Активные",
            "Повторные"});
            this.comboBox1.Location = new System.Drawing.Point(225, 9);
            this.comboBox1.Name = "comboBox1";
            this.comboBox1.Size = new System.Drawing.Size(121, 21);
            this.comboBox1.TabIndex = 2;
            this.comboBox1.SelectedIndexChanged += new System.EventHandler(this.comboBox1_SelectedIndexChanged);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(147, 12);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(72, 13);
            this.label3.TabIndex = 1;
            this.label3.Text = "Отображать:";
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(9, 7);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(113, 23);
            this.button1.TabIndex = 0;
            this.button1.Text = "Обновить список";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click_1);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSize = true;
            this.ClientSize = new System.Drawing.Size(924, 456);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.logArea);
            this.Name = "Form1";
            this.Text = "Регистратор. Сборка 1.15062012";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.panel1.ResumeLayout(false);
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.offset)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.panel3.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dataGridView1)).EndInit();
            this.panel4.ResumeLayout(false);
            this.panel2.ResumeLayout(false);
            this.panel2.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.OpenFileDialog openFileDialog1;
        private System.Windows.Forms.RichTextBox logArea;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.GroupBox groupBox1;
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
        private System.Windows.Forms.Panel panel2;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.ComboBox comboBox1;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Panel panel3;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.Button button4;
        private System.Windows.Forms.Button button3;
        private System.Windows.Forms.DataGridView dataGridView1;
        private System.Windows.Forms.DataGridViewTextBoxColumn IdColumn;
        private System.Windows.Forms.DataGridViewTextBoxColumn NameColumn;
        private System.Windows.Forms.DataGridViewTextBoxColumn TimeColumn;
        private System.Windows.Forms.Panel panel4;
        private System.Windows.Forms.Label offsetLabel;
        private System.Windows.Forms.TrackBar offset;
    }
}

