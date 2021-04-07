using System;
using System.Collections.Generic;
using MDMApi.Entity;
using System.Threading.Tasks;

namespace MDMApi.Repositories{
    public interface IAppRepository{
        Task<bool> CreateAppAsync(string name, string password, App app);
        Task<IEnumerable<string>> GetAppsAsync(string name, string password);
        Task<bool> DeletePolicyAsync(string name, string password, string appName);
    }
}