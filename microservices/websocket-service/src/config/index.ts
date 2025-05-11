interface Config {
  port: number;
  jwtSecret: string;
  nodeEnv: string;
  kafkaConfig: {
    brokers: string[];
    clientId: string;
    groupId: string;
  };
}

export const config: Config = {
  port: parseInt(process.env.WS_PORT || '3001', 10),
  jwtSecret: process.env.JWT_SECRET || 'your-default-secret-key',
  nodeEnv: process.env.NODE_ENV || 'development',
  kafkaConfig: {
    brokers: (process.env.KAFKA_BROKERS || 'localhost:9092').split(','),
    clientId: 'websocket-service',
    groupId: 'websocket-service-group'
  }
};
