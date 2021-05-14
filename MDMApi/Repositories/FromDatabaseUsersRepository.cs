using MDMApi.Entity;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;
using MDMApi.Database;

namespace MDMApi.Repositories{
    public class FromDatabaseUsersRepository: IUserRepository
    {
        DBService dbservice=new DBService();

        public async Task<bool> CreateUserAsync(User user){
            bool b=dbservice.AddUser(user);
            return await Task.FromResult(b);
        }
    }
}