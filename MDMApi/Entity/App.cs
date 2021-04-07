using System;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;
namespace MDMApi.Entity{
    public record App{
        [Key]
        public int App_Id{get; init;}
        public string Name{get; init;}
        
    }
}