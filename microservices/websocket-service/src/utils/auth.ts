import * as jwt from 'jsonwebtoken';
import { config } from '../config';

export async function verifyToken(token: string): Promise<string | null> {
  try {
    const decoded = jwt.verify(token, config.jwtSecret) as { userId: string };
    return decoded.userId;
  } catch (error) {
    console.error('Token verification failed:', error);
    return null;
  }
}
