using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Registrator
{
    public partial class RenameForm : Form
    {
        private PromoIdent promo_;
        public RenameForm(PromoIdent promo)
        {
            promo_ = promo;
            InitializeComponent();
            textBox1.Text = promo_.Name;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Result res = Loader.Load<Result>("rename&id=" + promo_.Id + "&name=" + textBox1.Text);
            if (res.result == "success")
            {
                this.DialogResult = DialogResult.OK;
                promo_.Name = textBox1.Text;
            }
            else
            {
                this.DialogResult = DialogResult.Cancel;
            }
            this.Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }
    }
}
