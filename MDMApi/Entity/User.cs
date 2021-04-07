using System;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;

namespace MDMApi.Entity{
    public record User{
        [Key]
        public int User_Id{get; init;}
        public string Name{get; init;}
        public string Email{get; init;}
        public string Password{get; init;}
    }
}