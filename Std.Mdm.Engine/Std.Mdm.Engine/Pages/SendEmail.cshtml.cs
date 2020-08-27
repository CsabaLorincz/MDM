using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Web;
using System.Net.Mail;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.UI;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.EntityFrameworkCore;
using Std.Mdm.Engine.Data;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using System.Text;
using System.Configuration;
using System.Globalization;
using System.ComponentModel;
using System.Collections.Specialized;
using System.Xml.Xsl;
using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.AspNetCore.Components;
using System.Data.Common;
using Microsoft.AspNetCore.Identity.UI.V3.Pages.Internal.Account.Manage;
using Microsoft.Extensions.Configuration.UserSecrets;

namespace Std.Mdm.Engine.Pages
{
    public class SendEmailModel : PageModel
    {
        private readonly IConfiguration Configuration;
        public SendEmailModel(IConfiguration configuration)
        {
            Configuration = configuration;
        }
        public void OnGet()
        {
            
            
            String body = "Testbody";
            String reciever = User.Identity.Name;
            
            String sender = Configuration["SenderEmail"];
            String password = Configuration["SenderPW"];
            MailMessage o = new MailMessage(sender, reciever, "Test Email 2", body);
            System.Net.NetworkCredential netCred = new System.Net.NetworkCredential(sender, password);
            SmtpClient smtpobj = new SmtpClient("smtp.gmail.com", 587);
            smtpobj.EnableSsl = true;
            smtpobj.Credentials = netCred;
            smtpobj.DeliveryMethod=SmtpDeliveryMethod.Network;
            smtpobj.Send(o);
            
        }
    }
}