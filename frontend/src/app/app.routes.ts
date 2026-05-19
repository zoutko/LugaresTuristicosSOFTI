import { Routes } from '@angular/router';
import { AuthPage } from './features/auth/auth-page/auth-page';
import { ChangePasswordPage } from './features/auth/change-password-page/change-password-page';
import { ProfilePage } from './features/users/users-page/profile-page';
import { SavedToursListComponent } from './features/users/users-action-page/saved-page/favorite-page';

export const routes: Routes = [
    { path: 'profile', component: ProfilePage },
    {path: 'recorridos-guardados', component: SavedToursListComponent },
    { path: 'auth/change-password', component: ChangePasswordPage },
    { path: 'auth', component: AuthPage, pathMatch: 'full' }
];
