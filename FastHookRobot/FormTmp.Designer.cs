namespace FastHook
{
    partial class FormTmp
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.txtTmp1 = new System.Windows.Forms.TextBox();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.txtTmp2 = new System.Windows.Forms.TextBox();
            this.tabPage3 = new System.Windows.Forms.TabPage();
            this.txtTmp3 = new System.Windows.Forms.TextBox();
            this.tabPage4 = new System.Windows.Forms.TabPage();
            this.textBox4 = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.textBox3 = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.button1 = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.tabPage5 = new System.Windows.Forms.TabPage();
            this.tabPage6 = new System.Windows.Forms.TabPage();
            this.txtTmp11 = new System.Windows.Forms.TextBox();
            this.txtTmp22 = new System.Windows.Forms.TextBox();
            this.tabPage7 = new System.Windows.Forms.TabPage();
            this.tabPage8 = new System.Windows.Forms.TabPage();
            this.txtTmp111 = new System.Windows.Forms.TextBox();
            this.txtTmp222 = new System.Windows.Forms.TextBox();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.tabPage3.SuspendLayout();
            this.tabPage4.SuspendLayout();
            this.tabPage5.SuspendLayout();
            this.tabPage6.SuspendLayout();
            this.tabPage7.SuspendLayout();
            this.tabPage8.SuspendLayout();
            this.SuspendLayout();
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage5);
            this.tabControl1.Controls.Add(this.tabPage7);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Controls.Add(this.tabPage6);
            this.tabControl1.Controls.Add(this.tabPage8);
            this.tabControl1.Controls.Add(this.tabPage3);
            this.tabControl1.Controls.Add(this.tabPage4);
            this.tabControl1.Location = new System.Drawing.Point(2, 1);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(536, 231);
            this.tabControl1.TabIndex = 0;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.txtTmp1);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(528, 205);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "比色";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // txtTmp1
            // 
            this.txtTmp1.Location = new System.Drawing.Point(3, 3);
            this.txtTmp1.Multiline = true;
            this.txtTmp1.Name = "txtTmp1";
            this.txtTmp1.Size = new System.Drawing.Size(522, 199);
            this.txtTmp1.TabIndex = 0;
            this.txtTmp1.Text = "If CmpColorEx([多点比色_颜色描述][0],0.9) = 1 Then\r\n\tTracePrint 1\r\nElse\r\n\tTracePrint 0\r\nE" +
    "nd If";
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.txtTmp2);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(528, 205);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "找色";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // txtTmp2
            // 
            this.txtTmp2.Location = new System.Drawing.Point(3, 3);
            this.txtTmp2.Multiline = true;
            this.txtTmp2.Name = "txtTmp2";
            this.txtTmp2.Size = new System.Drawing.Size(522, 199);
            this.txtTmp2.TabIndex = 1;
            this.txtTmp2.Text = "Dim intX,intY\r\nFindMultiColor [找色区域],[多点找色_颜色描述][0],0,0.9,intX,intY\r\nIf intX > -1" +
    " And intY > -1 Then\r\n\tTracePrint intX\r\n\tTracePrint intY\r\nEnd If";
            // 
            // tabPage3
            // 
            this.tabPage3.Controls.Add(this.txtTmp3);
            this.tabPage3.Location = new System.Drawing.Point(4, 22);
            this.tabPage3.Name = "tabPage3";
            this.tabPage3.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage3.Size = new System.Drawing.Size(528, 205);
            this.tabPage3.TabIndex = 2;
            this.tabPage3.Text = "文字识别";
            this.tabPage3.UseVisualStyleBackColor = true;
            // 
            // txtTmp3
            // 
            this.txtTmp3.Location = new System.Drawing.Point(3, 3);
            this.txtTmp3.Multiline = true;
            this.txtTmp3.Name = "txtTmp3";
            this.txtTmp3.Size = new System.Drawing.Size(522, 199);
            this.txtTmp3.TabIndex = 1;
            this.txtTmp3.Text = "Dim intX,intY\r\nFindColor [找色区域],[区域找色_颜色描述][0],0,0.9,intX,intY\r\nIf intX > -1 And " +
    "intY > -1 Then\r\n\tTracePrint intX\r\n\tTracePrint intY\r\nEnd If";
            // 
            // tabPage4
            // 
            this.tabPage4.Controls.Add(this.textBox4);
            this.tabPage4.Controls.Add(this.label3);
            this.tabPage4.Controls.Add(this.textBox3);
            this.tabPage4.Controls.Add(this.label4);
            this.tabPage4.Location = new System.Drawing.Point(4, 22);
            this.tabPage4.Name = "tabPage4";
            this.tabPage4.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage4.Size = new System.Drawing.Size(528, 205);
            this.tabPage4.TabIndex = 3;
            this.tabPage4.Text = "参数设置";
            this.tabPage4.UseVisualStyleBackColor = true;
            // 
            // textBox4
            // 
            this.textBox4.Location = new System.Drawing.Point(76, 9);
            this.textBox4.Name = "textBox4";
            this.textBox4.Size = new System.Drawing.Size(49, 21);
            this.textBox4.TabIndex = 7;
            this.textBox4.Text = "5";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(5, 13);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(65, 12);
            this.label3.TabIndex = 10;
            this.label3.Text = "生成几组：";
            // 
            // textBox3
            // 
            this.textBox3.Location = new System.Drawing.Point(76, 38);
            this.textBox3.Name = "textBox3";
            this.textBox3.Size = new System.Drawing.Size(153, 21);
            this.textBox3.TabIndex = 9;
            this.textBox3.Text = "000000";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(5, 42);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(65, 12);
            this.label4.TabIndex = 8;
            this.label4.Text = "生成偏色：";
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(12, 238);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(145, 39);
            this.button1.TabIndex = 1;
            this.button1.Text = "还原设置";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(359, 238);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(145, 39);
            this.button2.TabIndex = 2;
            this.button2.Text = "保存设置";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // tabPage5
            // 
            this.tabPage5.Controls.Add(this.txtTmp11);
            this.tabPage5.Location = new System.Drawing.Point(4, 22);
            this.tabPage5.Name = "tabPage5";
            this.tabPage5.Size = new System.Drawing.Size(528, 205);
            this.tabPage5.TabIndex = 4;
            this.tabPage5.Text = "比色点击";
            this.tabPage5.UseVisualStyleBackColor = true;
            // 
            // tabPage6
            // 
            this.tabPage6.Controls.Add(this.txtTmp22);
            this.tabPage6.Location = new System.Drawing.Point(4, 22);
            this.tabPage6.Name = "tabPage6";
            this.tabPage6.Size = new System.Drawing.Size(528, 205);
            this.tabPage6.TabIndex = 5;
            this.tabPage6.Text = "找色点击";
            this.tabPage6.UseVisualStyleBackColor = true;
            // 
            // txtTmp11
            // 
            this.txtTmp11.Location = new System.Drawing.Point(3, 3);
            this.txtTmp11.Multiline = true;
            this.txtTmp11.Name = "txtTmp11";
            this.txtTmp11.Size = new System.Drawing.Size(522, 199);
            this.txtTmp11.TabIndex = 1;
            this.txtTmp11.Text = "If CmpColorEx([多点比色_颜色描述][0],0.9) = 1 Then\r\n\tTracePrint 1\r\nElse\r\n\tTracePrint 0\r\nE" +
    "nd If";
            // 
            // txtTmp22
            // 
            this.txtTmp22.Location = new System.Drawing.Point(3, 3);
            this.txtTmp22.Multiline = true;
            this.txtTmp22.Name = "txtTmp22";
            this.txtTmp22.Size = new System.Drawing.Size(522, 199);
            this.txtTmp22.TabIndex = 2;
            this.txtTmp22.Text = "Dim intX,intY\r\nFindMultiColor [找色区域],[多点找色_颜色描述][0],0,0.9,intX,intY\r\nIf intX > -1" +
    " And intY > -1 Then\r\n\tTracePrint intX\r\n\tTracePrint intY\r\nEnd If";
            // 
            // tabPage7
            // 
            this.tabPage7.Controls.Add(this.txtTmp111);
            this.tabPage7.Location = new System.Drawing.Point(4, 22);
            this.tabPage7.Name = "tabPage7";
            this.tabPage7.Size = new System.Drawing.Size(528, 205);
            this.tabPage7.TabIndex = 6;
            this.tabPage7.Text = "比色点击判断";
            this.tabPage7.UseVisualStyleBackColor = true;
            // 
            // tabPage8
            // 
            this.tabPage8.Controls.Add(this.txtTmp222);
            this.tabPage8.Location = new System.Drawing.Point(4, 22);
            this.tabPage8.Name = "tabPage8";
            this.tabPage8.Size = new System.Drawing.Size(528, 205);
            this.tabPage8.TabIndex = 7;
            this.tabPage8.Text = "找色点击判断";
            this.tabPage8.UseVisualStyleBackColor = true;
            // 
            // txtTmp111
            // 
            this.txtTmp111.Location = new System.Drawing.Point(3, 3);
            this.txtTmp111.Multiline = true;
            this.txtTmp111.Name = "txtTmp111";
            this.txtTmp111.Size = new System.Drawing.Size(522, 199);
            this.txtTmp111.TabIndex = 2;
            this.txtTmp111.Text = "If CmpColorEx([多点比色_颜色描述][0],0.9) = 1 Then\r\n\tTracePrint 1\r\nElse\r\n\tTracePrint 0\r\nE" +
    "nd If";
            // 
            // txtTmp222
            // 
            this.txtTmp222.Location = new System.Drawing.Point(3, 3);
            this.txtTmp222.Multiline = true;
            this.txtTmp222.Name = "txtTmp222";
            this.txtTmp222.Size = new System.Drawing.Size(522, 199);
            this.txtTmp222.TabIndex = 3;
            this.txtTmp222.Text = "Dim intX,intY\r\nFindMultiColor [找色区域],[多点找色_颜色描述][0],0,0.9,intX,intY\r\nIf intX > -1" +
    " And intY > -1 Then\r\n\tTracePrint intX\r\n\tTracePrint intY\r\nEnd If";
            // 
            // FormTmp
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(540, 285);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.tabControl1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
            this.Name = "FormTmp";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "参数和模板设置";
            this.Load += new System.EventHandler(this.FormTmp_Load);
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage1.PerformLayout();
            this.tabPage2.ResumeLayout(false);
            this.tabPage2.PerformLayout();
            this.tabPage3.ResumeLayout(false);
            this.tabPage3.PerformLayout();
            this.tabPage4.ResumeLayout(false);
            this.tabPage4.PerformLayout();
            this.tabPage5.ResumeLayout(false);
            this.tabPage5.PerformLayout();
            this.tabPage6.ResumeLayout(false);
            this.tabPage6.PerformLayout();
            this.tabPage7.ResumeLayout(false);
            this.tabPage7.PerformLayout();
            this.tabPage8.ResumeLayout(false);
            this.tabPage8.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.TabPage tabPage3;
        public System.Windows.Forms.TextBox txtTmp1;
        public System.Windows.Forms.TextBox txtTmp2;
        public System.Windows.Forms.TextBox txtTmp3;
        private System.Windows.Forms.TabPage tabPage4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        public System.Windows.Forms.TextBox textBox4;
        public System.Windows.Forms.TextBox textBox3;
        private System.Windows.Forms.TabPage tabPage5;
        public System.Windows.Forms.TextBox txtTmp11;
        private System.Windows.Forms.TabPage tabPage6;
        public System.Windows.Forms.TextBox txtTmp22;
        private System.Windows.Forms.TabPage tabPage7;
        private System.Windows.Forms.TabPage tabPage8;
        public System.Windows.Forms.TextBox txtTmp111;
        public System.Windows.Forms.TextBox txtTmp222;
    }
}