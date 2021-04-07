using System;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;

namespace MDMApi.Entity{
    public record Policy{
        [Key]
        public int Pol_Id{get; init;}
        public int User_Id{get; init;}
        public int App_Id{get; init;}
        
    }
}