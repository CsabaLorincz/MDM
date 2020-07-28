using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Web;
using System.Net.Mail;



namespace Std.Mdm.Engine.Pages
{
    public class SendEmailModel : PageModel
    {
        public void OnGet()
        {
            String body = "Testbody";
            String ppath = "D:\\Source\\Sapientia\\1234.txt";
            System.IO.StreamReader reader = new System.IO.StreamReader(ppath);
            String password;
            password = reader.ReadLine();
            String reciever = User.Identity.Name;
            MailMessage o = new MailMessage("lorincz.csaba99@gmail.com", reciever, "Test Email 2", body);
            System.Net.NetworkCredential netCred = new System.Net.NetworkCredential("lorincz.csaba99@gmail.com", password);
            SmtpClient smtpobj = new SmtpClient("smtp.gmail.com", 587);
            smtpobj.EnableSsl = true;
            smtpobj.Credentials = netCred;
            smtpobj.DeliveryMethod=SmtpDeliveryMethod.Network;
            smtpobj.Send(o);
        }
    }
}