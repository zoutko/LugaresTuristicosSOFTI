import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


/**
 * Rol del usuario (viene del security module)
 * Corresponde a RoleDTO del backend
 */
export interface RoleDTO {
  id: number;
  name: string;  // ej: "ADMINISTRATOR", "USER", etc.
}

/**
 * Contacto del usuario
 * Corresponde a ContactResponse del backend
 */
export interface ContactResponse {
  id: number;
  phoneNumber: string;
}

/**
 * Perfil del usuario (datos personales)
 * Corresponde a UserProfileResponse del backend
 */
export interface UserProfileResponse {
  id: number;
  name: string;
  document: string;
  role: RoleDTO;
  contacts: ContactResponse[];
}

/**
 * Respuesta completa del usuario (email + perfil)
 * Corresponde a UserResponse del backend
 * GET /api/users/{userId}
 * POST /api/users/register
 */
export interface UserResponse {
  id: number;
  email: string;
  profile: UserProfileResponse;
}

/**
 * Petición para crear un usuario (registro)
 * Corresponde a CreateUserRequest del backend
 * POST /api/users/register
 */
export interface CreateUserRequest {
  name: string;
  document: string;
  roleName?: string;        // opcional, puede tener valor por defecto
  phoneNumbers?: string[];  // opcional, lista de teléfonos
  email: string;
  password: string;
}

/**
 * Petición para actualizar un CAMPO específico del usuario
 * Corresponde a UpdateUserRequest del backend
 * PATCH /api/users/{userId}
 */
export interface UpdateUserRequest {
  field: string;      // ej: "name", "email", "document"
  newValue: string;
}

/**
 * Petición para actualizar un contacto (teléfono)
 * Corresponde a UpdateContactRequest del backend
 * POST /api/users/{userId}/contacts
 * PATCH /api/users/{userId}/contacts
 */
export interface UpdateContactRequest {
  contactId?: number;    // opcional para POST (crear), obligatorio para PATCH (actualizar)
  phoneNumber: string;
}

// ============================================
// SERVICIO
// ============================================

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private readonly http: HttpClient) {}

  /**
   * Obtener perfil de usuario
   * GET /api/users/{userId}
   */
  
  getProfile(userId: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`/api/users/${userId}`);
  }

  /**
   * Actualizar un campo específico del usuario
   * PATCH /api/users/{userId}
   * @param userId ID del usuario
   * @param field Campo a actualizar ("name", "email", "document")
   * @param newValue Nuevo valor
   */
  updateProfileField(userId: number, field: string, newValue: string): Observable<UserResponse> {
    const request: UpdateUserRequest = { field, newValue };
    if(field === "email"){
      return this.http.patch<UserResponse>(`/api/auth/change-email`, request);
    }else{
      return this.http.patch<UserResponse>(`/api/users/${userId}`, request);
    }
    
  }

  /**
   * Actualizar múltiples campos (conveniencia - hace múltiples PATCH o un solo custom)
   * Nota: El backend actualmente soporta un campo por PATCH.
   * Para múltiples campos, hay que hacer múltiples llamadas.
   */
  updateProfile(userId: number, updates: { field: string; newValue: string }[]): Observable<UserResponse>[] {
    return updates.map(update => this.updateProfileField(userId, update.field, update.newValue));
  }

  /**
   * Eliminar cuenta de usuario
   * DELETE /api/users/{userId}
   */
  deleteAccount(userId: number): Observable<void> {
    return this.http.delete<void>(`/api/users/${userId}`);
  }

  /**
   * Agregar un contacto (número de teléfono)
   * POST /api/users/{userId}/contacts
   */
  addContact(userId: number, phoneNumber: string): Observable<UserResponse> {
    const request: UpdateContactRequest = { phoneNumber };
    return this.http.post<UserResponse>(`/api/users/${userId}/contacts`, request);
  }

  /**
   * Actualizar un contacto existente
   * PATCH /api/users/{userId}/contacts
   */
  updateContact(userId: number, contactId: number, phoneNumber: string): Observable<UserResponse> {
    const request: UpdateContactRequest = { contactId, phoneNumber };
    return this.http.patch<UserResponse>(`/api/users/${userId}/contacts`, request);
  }

  /**
   * Eliminar un contacto
   * DELETE /api/users/{userId}/contacts/{contactId}
   */
  deleteContact(userId: number, contactId: number): Observable<void> {
    return this.http.delete<void>(`/api/users/${userId}/contacts/${contactId}`);
  }

  createUser(request: CreateUserRequest): Observable<UserResponse> {
  return this.http.post<UserResponse>('/api/users/register', request);
}
}