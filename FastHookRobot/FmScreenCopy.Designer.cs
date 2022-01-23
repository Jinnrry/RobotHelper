namespace FastHook
{
    partial class FmScreenCopy
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
            this.SuspendLayout();
            // 
            // FmScreenCopy
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(0)))), ((int)(((byte)(192)))), ((int)(((byte)(192)))));
            this.ClientSize = new System.Drawing.Size(163, 130);
            this.Cursor = System.Windows.Forms.Cursors.Cross;
            this.DoubleBuffered = true;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "FmScreenCopy";
            this.Opacity = 0.9D;
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.Text = "FmScreenCopy";
            this.Load += new System.EventHandler(this.FmScreenCopy_Load);
            this.MouseClick += new System.Windows.Forms.MouseEventHandler(this.FmScreenCopy_MouseClick);
            this.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.FmScreenCopy_MouseDoubleClick);
            this.MouseDown += new System.Windows.Forms.MouseEventHandler(this.FmScreenCopy_MouseDown);
            this.MouseMove += new System.Windows.Forms.MouseEventHandler(this.FmScreenCopy_MouseMove);
            this.MouseUp += new System.Windows.Forms.MouseEventHandler(this.FmScreenCopy_MouseUp);
            this.ResumeLayout(false);

        }

        #endregion
    }
}