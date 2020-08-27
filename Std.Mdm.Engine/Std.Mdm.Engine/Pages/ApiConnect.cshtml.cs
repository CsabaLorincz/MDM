using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;

namespace Std.Mdm.Engine.Pages
{
    public class ApiConnectModel : PageModel
    {
        private static readonly HttpClient client = new HttpClient();
        private static async Task ProcessRepositories()
        {
            client.DefaultRequestHeaders.Accept.Clear();
            // client.DefaultRequestHeaders.Accept.Add(
            //  new MediaTypeWithQualityHeaderValue("application/vnd.github.v3+json"));
            // client.DefaultRequestHeaders.Add("User-Agent", ".NET Foundation Repository Reporter");
            var stringTask = client.GetStringAsync("http://192.168.56.101:8080/hmdm");
            var msg = await stringTask;
            System.Diagnostics.Debug.WriteLine(msg);
            
        }
       
        public async void OnGet()
        {
            await ProcessRepositories();

        }
    }
}