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
    [Route("users")]
    public class UserController : ControllerBase
    {
        private readonly IUserRepository repository;

        public UserController(IUserRepository repository)
        {
            this.repository = repository;
        }


        //Get/Item/{id}
        [HttpPost]
        public async Task<bool> CreateUserAsync(User user)
        {
            return await repository.CreateUserAsync(user);
        }

        
    }
}