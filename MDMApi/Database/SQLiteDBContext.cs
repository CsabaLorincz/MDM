using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MDMApi.Entity;
using static Microsoft.EntityFrameworkCore.Infrastructure.IDbContextOptionsBuilderInfrastructure;

namespace MDMApi.Database
{
    public class SQLiteDBContext: DbContext
    {
        public DbSet<App> Apps { get; set; }
        public DbSet<User> Users{get; set;}
        public DbSet<Policy> Policies{get;set;}
        protected override void OnConfiguring(DbContextOptionsBuilder options)
           => options.UseSqlite("Data Source=sqlitedemo.db");
    }
}
