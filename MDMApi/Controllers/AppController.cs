using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.AspNetCore.Mvc;
using MDMApi.Entity;
using MDMApi.Repositories;
using System.Threading.Tasks;
using MDMApi.Support;
namespace MDMApi.Controllers{
    [ApiController]
    [Route("items")]
    public class AppController : ControllerBase
    {
        private readonly IAppRepository repository;

        public AppController(IAppRepository repository)
        {
            this.repository = repository;
        }


        //Get/Item/{id}
        [HttpGet("name={name}&pw={password}")]
        public async Task<IEnumerable<string>> GetAppsAsync(string name, string password)
        {
            return await repository.GetAppsAsync(name, password);
        }
        [HttpPost("name={name}&pw={password}")]
        public async Task<bool> CreateAppAsync(string name, string password, App app){
            return await repository.CreateAppAsync(name, password, app);
        }

        [HttpDelete("name={name}&pw={password}&appName={appName}")]
        public async Task<bool> DeletePolicyAsync(string name, string password, string appName){
            return await repository.DeletePolicyAsync(name, password, appName);
        }



        
    }
}