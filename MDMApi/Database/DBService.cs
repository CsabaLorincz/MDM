using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MDMApi.Entity;
namespace MDMApi.Database
{
    public class DBService
    {
        
        private bool ValidUser(string name, string password){
            using (var db = new SQLiteDBContext())
            {
                return db.Users.Any(x => x.Name == name && x.Password==password);
            }
        }
        
        private bool ValidUser(string name){
            using (var db = new SQLiteDBContext())
            {
                return db.Users.Any(x => x.Name == name);
            }
        }
        private int GetUserId(string name){
            using (var db = new SQLiteDBContext())
            {
                var result = db.Users.FirstOrDefault( r => r.Name == name );
                return result.User_Id;
            }
        }

        public List<string> GetAppsFrDb(string name, string password)
        {
           if(ValidUser(name, password)){
                using (var db = new SQLiteDBContext())
                {
                    var id=GetUserId(name);
                    var x=from us in db.Users
                        join pol in db.Policies on us.User_Id equals pol.User_Id
                        join app in db.Apps on pol.App_Id equals  app.App_Id
                        where id == pol.User_Id
                        select app.Name;
                    var list=new List<string>();
                    int counter=0;
                    foreach(var i in x){
                        list.Add(i);
                        ++counter;
                    }
                    return list;
                }    
           }
           else{
               List<string> list = new List<string>();
               list.Add("Login Error");
               return list;
           }
 
        }
        
        private bool EmailCheck(string email){
            using (var db = new SQLiteDBContext())
            {
                return db.Users.Any(x => x.Email == email);
            }
        }

        public bool AddUser(User user){
            if(ValidUser(user.Name))
            {
                return false;
            }
            if(EmailCheck(user.Email))
                return false;
            using (var db = new SQLiteDBContext())
            {
                db.Users.Add(user);
                db.SaveChanges();
            }
            return true;
        }

        private bool IsAppAlreadyPresent(string appName){
            using (var db = new SQLiteDBContext())
            {
                return db.Apps.Any(x => x.Name == appName);
            }
        }

        private bool AddApp(App app){
            if(IsAppAlreadyPresent(app.Name))
            {
                return false;
            }
            using (var db = new SQLiteDBContext())
            {
                db.Apps.Add(app);
                db.SaveChanges();
            }
            return true;
        }

        private int GetAppId(string name){
            using (var db = new SQLiteDBContext())
            {
                var result = db.Apps.FirstOrDefault( r => r.Name == name );
                return result.App_Id;
            }
        }

        private bool SetAlready(Policy pol){
            using (var db = new SQLiteDBContext())
            {
                return db.Policies.Any(x => x.User_Id == pol.User_Id && x.App_Id==pol.App_Id);
            }
        }

        private bool AddPolicy(Policy pol){
            if(SetAlready(pol)){
                return false;
            }
            using (var db = new SQLiteDBContext())
            {
                db.Policies.Add(pol);
                db.SaveChanges();
            }
            return true;
        }

        public bool AddApp(string name, string password, App app){
            if(!ValidUser(name, password)){
                return false;
            }
            AddApp(app);
            var u_id=GetUserId(name);
            var app_id=GetAppId(app.Name);
            Policy pol=new Policy(){
                User_Id=u_id,
                App_Id=app_id
            };
            return AddPolicy(pol);
        }

        private Policy GetPolicy(int u_id, int app_id){
            using (var db = new SQLiteDBContext())
            {
            return db.Policies.FirstOrDefault(x => x.User_Id == u_id && x.App_Id==app_id);
            }
        }

        public bool DeletePolicy(string name, string password, string appName){
            if(!ValidUser(name, password)){
                return false;
            }
            if(!IsAppAlreadyPresent(appName)){
                return false;
            }

            Policy pol=GetPolicy(GetUserId(name), GetAppId(appName));
            if(pol==null)
                return false;
            using (var db = new SQLiteDBContext())
            {
                db.Policies.Remove(pol);
                db.SaveChanges();
            }
            return true;
        }
        
    }
}
