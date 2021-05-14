using MDMApi.Entity;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;
using MDMApi.Database;

namespace MDMApi.Repositories{
    public class FromDatabaseAppsRepository: IAppRepository
    {
        DBService dbservice=new DBService();
        public async Task<bool> CreateAppAsync(string name, string password, App app){
            bool b=dbservice.AddApp(name, password, app);
            return await Task.FromResult(b);
        }
        public async Task<IEnumerable<string>> GetAppsAsync(string name, string password){
            List<string> lis=dbservice.getAppsFrDb(name, password);
            return await Task.FromResult(lis);
        }
        public async Task<bool> DeletePolicyAsync(string name, string password, string appName){
            bool b=dbservice.DeletePolicy(name, password, appName);
            return await Task.FromResult(b);        }
    }
}