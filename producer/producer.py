import pika, logging, sys, argparse
from argparse import RawTextHelpFormatter

if __name__ == '__main__':
    examples = sys.argv[0] + " -p 5672 -s rabbitmq -m 'Hello from Danelle' "
    parser = argparse.ArgumentParser(formatter_class=RawTextHelpFormatter,
                                 description='Run producer.py',
                                 epilog=examples)
    parser.add_argument('-p', '--port', action='store', dest='port', help='The port to listen on.')
    parser.add_argument('-s', '--server', action='store', dest='server', help='The RabbitMQ server.')
    parser.add_argument('-m', '--message', action='store', dest='message', help='The message to send')

    args = parser.parse_args()
    if args.port == None:
        print "Missing required argument: -p/--port"
        sys.exit(1)
    if args.server == None:
        print "Missing required argument: -s/--server"
        sys.exit(1)
    if args.message == None:
        print "Missing argument: -m/--message. Defaulting to Hello World"
        message = "Hello World"
    else:
        message = args.message

    logging.basicConfig(level=logging.INFO)
    LOG = logging.getLogger(__name__)
    credentials = pika.PlainCredentials('guest', 'guest')
    parameters = pika.ConnectionParameters(args.server,
                                           int(args.port),
                                           '/',
                                           credentials)
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    q = channel.queue_declare('pc')
    q_name = q.method.queue

    # Turn on delivery confirmations
    channel.confirm_delivery()

    if channel.basic_publish('', q_name, message):
        LOG.info('Message has been delivered')
    else:
        LOG.warning('Message NOT delivered')

    connection.close()