create database tcc;

create table usuario(
id int primary key auto_increment,
nome varchar(50),
email varchar(50),
senha varchar(50),
idCidade int
);

create table prefeitura(
id int primary key auto_increment,
nome varchar(50),
idCidade int
);

create table servico(
id int primary key auto_increment,
nome varchar(50),
descricao varchar(50),
idCidade int
);

create table servicos(
id int primary key auto_increment,
idServico int,
idPrefeitura int,
CONSTRAINT FK_Servico FOREIGN KEY (idServico) REFERENCES servico(id),
CONSTRAINT FK_Prefeitura FOREIGN KEY (idPrefeitura) REFERENCES prefeitura(id)
);

create table cidade(
id int primary key auto_increment,
nome varchar(50),
estado varchar(50)
);

create table publicacao(
id int primary key auto_increment,
titulo varchar(100),
descricao varchar(600),
localizacao varchar(200),
idServico int,
curtidas int,
idCidade int
);

create table imagem(
id int primary key auto_increment,
idRequerimento int,
codigo varchar(500),
CONSTRAINT FK_Imagem FOREIGN KEY (idRequerimento) REFERENCES publicacao(id)
);

create table comentario(
id int primary key auto_increment,
comentario varchar(300),
curtida int,
usuario_id int,
publicacao_id int
);

select * from cidade;

select * from imagem;

select * from prefeitura;

select * from publicacao;

select * from servico;

select * from usuario;

select * from comentario;


insert into servico(nome) values("Iluminação"), ("Limpeza"), ("Reforma"), ("Segurança"), ("Lazer"), ("Sinalização");

insert into publicacao(titulo, descricao, localizacao, idServico, curtidas) values("Investimento na Educação: Uma Prioridade para o Futuro da Cidade", "A prefeitura está no caminho certo, mas acredito que poderiam investir mais na educação. A construção de novas escolas e a melhoria na qualidade do ensino seriam ótimas maneiras de garantir um futuro melhor para nossas crianças.", "R. Lindóia, 118-298 - Vila Sao Cristovao, Valinhos - SP, 13276-550", 1, 50),
																				  ("Estradas Precisam de Reparos Urgentes na Cidade", "Precisamos urgentemente de um programa de recuperação das estradas da cidade. Os buracos estão causando danos aos veículos e representam riscos para a segurança dos motoristas. Espero que a prefeitura tome medidas para resolver essa questão.", "R. Lindóia, 118-298 - Vila Sao Cristovao, Valinhos - SP, 13276-550", 2, 30),
																				  ("Programas de Capacitação para Jovens: Oportunidade e Desenvolvimento", "Uma sugestão importante é aumentar a oferta de programas de capacitação e empregabilidade para jovens. Isso não apenas reduziria o desemprego entre os jovens, mas também contribuiria para o desenvolvimento econômico de nossa cidade.", "R. Lindóia, 118-298 - Vila Sao Cristovao, Valinhos - SP, 13276-550", 3, 45),
																				  ("Reciclagem e Coleta Seletiva: Cuidando do Meio Ambiente da Cidade", "A prefeitura poderia implementar medidas mais eficazes para a coleta seletiva de lixo e a reciclagem. Isso ajudaria a reduzir nossa pegada de carbono e preservar nosso meio ambiente.", "R. Lindóia, 118-298 - Vila Sao Cristovao, Valinhos - SP, 13276-550", 4, 15),
																				  ("Mais Espaços Verdes e Lazer para Todas as Famílias", "Gostaria de ver mais espaços verdes e áreas de lazer para as famílias em nossa cidade. Parques bem mantidos, trilhas para caminhada e áreas para piquenique seriam um ótimo investimento para o bem-estar de todos.", "R. Lindóia, 118-298 - Vila Sao Cristovao, Valinhos - SP, 13276-550", 5, 20);

insert into comentario(comentario, curtida, usuario_id, publicacao_id) values("Concordo totalmente! A educação é o alicerce do nosso futuro, e investir em nossas escolas é fundamental. Além disso, a prefeitura poderia criar programas extracurriculares para enriquecer o aprendizado das crianças.", 5, 1, 1),
																		("As estradas estão um desastre! Eu mesma já tive que pagar por reparos no meu carro devido aos buracos. A prefeitura deveria priorizar a infraestrutura viária para garantir a segurança dos motoristas.", 8, 1, 2),
																		("Ótima sugestão. Programas de capacitação e empregabilidade para jovens são essenciais para prepará-los para o mercado de trabalho. Isso poderia ajudar a reduzir a taxa de desemprego juvenil em nossa cidade.", 15, 2, 3),
																		("Eu também acho que a reciclagem é crucial para a nossa cidade. A conscientização ambiental é essencial, e a prefeitura pode incentivar a separação de resíduos e a reciclagem oferecendo programas educacionais e pontos de coleta.", 32, 3, 4),
																		("Mais espaços verdes e áreas de lazer seriam maravilhosos para nossas famílias. Além de promover um estilo de vida saudável, isso proporcionaria momentos de relaxamento e convívio social.", 20, 4, 5);