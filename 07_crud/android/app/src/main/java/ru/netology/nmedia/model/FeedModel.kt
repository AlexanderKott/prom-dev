package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

 


sealed class FeedModel
data  class PostsFeed (val posts: List<Post> = emptyList()) : FeedModel()
data  class LoadingFeed (val loading : Boolean) : FeedModel()
data  class ErrorFeed (val error : Boolean) : FeedModel()
data  class EmptyFeed (val empty : Boolean) : FeedModel()
data  class RefreshingFeed (val empty : Boolean) : FeedModel()


 
