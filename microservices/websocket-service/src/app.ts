import * as http from 'http';
import * as WebSocket from 'ws';
import * as express from 'express';
import * as dotenv from 'dotenv';
import { config } from './config';
import { verifyToken } from './utils/auth';

dotenv.config();

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

interface ExtWebSocket extends WebSocket {
  userId?: string;
  subscriptions?: Set<string>;
}

// Store active connections
const clients = new Map<string, ExtWebSocket>();

wss.on('connection', async (ws: ExtWebSocket, req) => {
  try {
    const token = req.url?.split('token=')[1];
    if (!token) {
      ws.close(1008, 'Authentication required');
      return;
    }

    const userId = await verifyToken(token);
    if (!userId) {
      ws.close(1008, 'Invalid token');
      return;
    }

    ws.userId = userId;
    ws.subscriptions = new Set();
    clients.set(userId, ws);

    console.log(`Client connected: ${userId}`);

    ws.on('message', async (message: string) => {
      try {
        const data = JSON.parse(message);
        handleMessage(ws, data);
      } catch (error) {
        console.error('Error handling message:', error);
        ws.send(JSON.stringify({ error: 'Invalid message format' }));
      }
    });

    ws.on('close', () => {
      if (ws.userId) {
        clients.delete(ws.userId);
        console.log(`Client disconnected: ${ws.userId}`);
      }
    });

  } catch (error) {
    console.error('Connection error:', error);
    ws.close(1011, 'Internal server error');
  }
});

function handleMessage(ws: ExtWebSocket, data: any) {
  switch (data.type) {
    case 'subscribe':
      handleSubscribe(ws, data);
      break;
    case 'unsubscribe':
      handleUnsubscribe(ws, data);
      break;
    default:
      ws.send(JSON.stringify({ error: 'Unknown message type' }));
  }
}

function handleSubscribe(ws: ExtWebSocket, data: any) {
  if (!data.channel) {
    ws.send(JSON.stringify({ error: 'Channel not specified' }));
    return;
  }

  ws.subscriptions?.add(data.channel);
  ws.send(JSON.stringify({
    type: 'subscribed',
    channel: data.channel
  }));
}

function handleUnsubscribe(ws: ExtWebSocket, data: any) {
  if (!data.channel) {
    ws.send(JSON.stringify({ error: 'Channel not specified' }));
    return;
  }

  ws.subscriptions?.delete(data.channel);
  ws.send(JSON.stringify({
    type: 'unsubscribed',
    channel: data.channel
  }));
}

// Broadcast market data to subscribed clients
export function broadcastMarketData(channel: string, data: any) {
  const message = JSON.stringify({
    type: 'market_data',
    channel,
    data
  });

  clients.forEach((ws) => {
    if (ws.subscriptions?.has(channel)) {
      ws.send(message);
    }
  });
}

// Broadcast order updates to specific user
export function broadcastOrderUpdate(userId: string, data: any) {
  const ws = clients.get(userId);
  if (ws) {
    ws.send(JSON.stringify({
      type: 'order_update',
      data
    }));
  }
}

const PORT = config.port;

server.listen(PORT, () => {
  console.log(`WebSocket server is running on port ${PORT}`);
});
