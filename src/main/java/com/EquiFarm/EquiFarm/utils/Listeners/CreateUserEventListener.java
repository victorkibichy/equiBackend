// package com.EquiFarm.EquiFarm.utils.Listeners;

// import org.springframework.context.event.EventListener;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;

// import com.EquiFarm.EquiFarm.Farmer.Farmer;
// import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
// import com.EquiFarm.EquiFarm.user.Role;
// import com.EquiFarm.EquiFarm.user.User;
// import com.EquiFarm.EquiFarm.utils.Events.UserCreatedEvent;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class CreateUserEventListener {

//     private final FarmerRepository farmerRepository;

//     @Async
//     @EventListener
//     public void handleUserCreatedEvent(UserCreatedEvent event) {
        
//         User user = event.getUser();
//         System.out.println("Create Profile for--->" + user.getEmail());

//         if (user.getRole() == Role.ADMIN || user.getIsAdmin() == true || user.getIsStaff()) {
//             System.out.println("Admin User:" + user.getEmail());
//         } else if (user.getRole() == Role.FARMER) {
//             Farmer farmer = new Farmer();
//             farmer.setUser(user);

//             farmerRepository.save(farmer);
//         }

//     }
// }
