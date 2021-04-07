using System;
using System.Collections.Generic;
using MDMApi.Entity;
using System.Threading.Tasks;

namespace MDMApi.Repositories{
    public interface IUserRepository{
        Task<bool> CreateUserAsync(User user);
    }
}